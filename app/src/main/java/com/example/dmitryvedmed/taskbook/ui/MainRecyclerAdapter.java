package com.example.dmitryvedmed.taskbook.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.NotifyTaskReceiver;
import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperAdapter;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperViewHolder;
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.logic.SimpleTask;
import com.example.dmitryvedmed.taskbook.logic.SuperTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.id.headTextView;
import static com.example.dmitryvedmed.taskbook.R.id.taskTextView;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {


    private List<SuperTask> tasks;
    private List<SuperTask> selectedTasks;
    private Context context;
    private ListTask listTask;
    private TextView textView;
    private Typeface typeFace;
    private Typeface boldTypeFace ;
    private DrawerTestActivity activity;
    boolean wasSelected;
    private Mode mode;
    private int selectedTasksCounter;
    private boolean[] selects;
    int getSelectedTasksCounter() {
        return selectedTasksCounter;
    }

    enum Mode {
        NORMAL, SELECTION_MODE
    }
    Mode getMode() {
        return mode;
    }
    void setSelectionMode(Mode mode){
        this.mode = mode;
    }

    List<SuperTask> getTasks() {
        return tasks;
    }

    MainRecyclerAdapter(List<SuperTask> tasks, Context context) {
        Log.d("TAG", "       Adapter --- constructor  ---");
        this.tasks = tasks;

        compareTasks();

        this.context = context;
        selectedTasks = new ArrayList<>();
        activity = (DrawerTestActivity) context;
        Log.d("TAG", "       Adapter, tasksSize = " + tasks.size());
        textView = new TextView(context);
        textView.setText("1234we5r");
        typeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
        boldTypeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
        mode = Mode.NORMAL;
        selects = new boolean[tasks.size()];

    }

    private void cancelNotification(int requestCode){

        Intent intent = new Intent(context, NotifyTaskReceiver.class);
        intent.setAction("TASK_NOTIFICATION");
        //intent1.putExtra("id", task.getId());
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

    @Override
    public void onItemDismiss(int position) {
        Log.d("TAG", "       Adapter --- onItemDismiss, position = " + position);
        tasks.get(position).setPosition(0);                 //?
        tasks.get(position).setRemind(false);                 //?
        activity.dbHelper.updateTask(tasks.get(position), Constants.DELETED);
        cancelNotification(tasks.get(position).getId());
        tasks.remove(position);
        notifyItemRemoved(position);
        setRightPosition();
        activity.showSnackBar(1);
    }

    public void deleteSelectedTasks(){
        compareSelectionTasks();
        for (SuperTask t:selectedTasks
                ) {
            activity.dbHelper.updateTask(t, Constants.DELETED);
        }

        tasks.removeAll(selectedTasks);
        selectedTasks.clear();
        setRightPosition();
        notifyDataSetChanged();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
        selects = new boolean[tasks.size()];
    }

    public void cancelSelection() {
        selectedTasks.clear();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        notifyDataSetChanged();
        selectedTasksCounter = 0;
        selects = new boolean[tasks.size()];
    }

    public void deleteSelectedTasksForever() {
        compareSelectionTasks();
        for (SuperTask t : selectedTasks
                ) {
            activity.dbHelper.deleteBook(t);
        }
        tasks.removeAll(selectedTasks);
        selectedTasks.clear();
        setRightPosition();
        notifyDataSetChanged();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
    }


    public void setColorSelectionTasks(int color){
        for (SuperTask s:selectedTasks
                ) {
            s.setColor(color);
        }
        notifyDataSetChanged();
        selectedTasks.clear();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
        selects = new boolean[tasks.size()];
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d("TAG", "       Adapter --- onItemMove, FROM - " + fromPosition + ", TO - " + toPosition);
        SuperTask prev = tasks.remove(fromPosition);
        tasks.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        setRightPosition();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private TextView stHeadLine, stContent, listHeadEditText, ltFirst, ltSecond;
        private LinearLayout layout;
        private CardView cardView;
        private ImageView alarm;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            stHeadLine = (TextView) itemView.findViewById(headTextView);
            stContent = (TextView) itemView.findViewById(taskTextView);
            alarm = (ImageView) itemView.findViewById(R.id.alarm_ic);
            alarm.setVisibility(View.GONE);


            if(stContent!=null) {
                stHeadLine.setTypeface(boldTypeFace);
                stContent.setTypeface(typeFace);
            }

            listHeadEditText = (TextView) itemView.findViewById(R.id.mainRecListItemHead);

            layout = (LinearLayout) itemView.findViewById(R.id.card_view_list_layout);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            //cardView.setOnLongClickListener((DrawerTestActivity)context);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);

        }

        @Override
        public void onItemSelected() {
            Log.d("TAG", "       Adapter --- onItemSelected");
            if(getAdapterPosition() == -1)
                return;
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorCardViewPressed));
            wasSelected = true;
            selectedTasks.add(tasks.get(getAdapterPosition()));
        }

        @Override
        public void onItemClear() {
            Log.d("TAG", "       Adapter --- onItemClear");
            wasSelected = false;
            selectedTasks.clear();
            if(getAdapterPosition() != -1)
                setColorCardView(cardView, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            Log.d("TAG", "       Adapter --- onClick " + position);
            Log.d("TAG", "       Adapter --- onClick " + view.toString());

            if(mode == Mode.NORMAL) {
                Log.d("TAG", "       Adapter --- MODE NORMAL " + MainRecyclerAdapter.this.getItemViewType(position) );

                switch (MainRecyclerAdapter.this.getItemViewType(position)) {
                    case 0:
                        Intent intent = new Intent(context, SimpleTaskActivity.class);
                        intent.putExtra("Task", (Serializable) tasks.get(position));
                        intent.putExtra("kind", activity.currentKind);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    case 1:
                        Intent intent1 = new Intent(context, ListTaskActivity.class);
                        intent1.putExtra("ListTask", tasks.get(position));
                        intent1.putExtra("kind", activity.currentKind);
                        context.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                }
            }
            else if (mode == Mode.SELECTION_MODE) {
                Log.d("TAG", "       Adapter --- MODE NE NORMAL " );

                if (selects[position]) {
                    Log.d("TAG", "       SELECTED POSITION " + position +" теперь False" );
                    // cardView.setCardBackgroundColor(Color.WHITE);
                    selectedTasks.remove(tasks.get(position));
                   // cardView.setSelected(false);
                    selects[position] = false;
                    selectedTasksCounter--;
                    activity.selectedItemCount(selectedTasksCounter);
                    if(selectedTasksCounter==0)
                        setSelectionMode(Mode.NORMAL);
                    Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                } else {
                   // cardView.setCardBackgroundColor(Color.LTGRAY);
                 //   cardView.setSelected(true);
                    Log.d("TAG", "       SELECTED POSITION " + position +" теперь True" );
                    selectedTasks.add(tasks.get(position));
                    selectedTasksCounter++;
                    activity.selectedItemCount(selectedTasksCounter);
                    selects[position] = true;
                    Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                }
                Log.d("TAG", "       Adapter --- sel. size" + selectedTasks.size());
            }
            notifyItemChanged(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("TAG", "      LOOOOONG CLICK " );
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("TAG", "      ON TOUCH" );
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    activity.hideFabs();
                    break;
            }
            return false;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TAG", "       Adapter --- onCreateViewHolder");
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_simple_task, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list_task, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    private void setColorCardView(CardView cardView, int position){
        Log.d("TAG", "       setColorCardView");
        if(mode == Mode.SELECTION_MODE) {
            Log.d("TAG", "       Adapter --- setColorCardView SELECTION MODE ON ");
            Log.d("TAG", "       SelectedTasks Size = " + selectedTasks.size() + " position = " + position);

            if (selectedTasks.contains(tasks.get(position))) {
                Log.d("TAG", "       SelectedTasks contains = " + position);
                cardView.setCardBackgroundColor(Color.LTGRAY);
            } else {

                Log.d("TAG", "       Adapter --- NOT SELECTION");
                if (position >= tasks.size())
                    return;
                switch (tasks.get(position).getColor()) {
                    case Constants.GREEN:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightGreen));
                        break;
                    case Constants.RED:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightRed));
                        break;
                    case Constants.YELLOW:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightYellow));
                        break;
                    case Constants.BLUE:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightLightBlue));
                        break;
                    case 0:
                        cardView.setCardBackgroundColor(Color.WHITE);
                        break;
                }
            }
            return;
        }
        if (position >= tasks.size())
            return;
        switch (tasks.get(position).getColor()) {
            case Constants.GREEN:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightGreen));
                break;
            case Constants.RED:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightRed));
                break;
            case Constants.YELLOW:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightYellow));
                break;
            case Constants.BLUE:
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.taskColorLightLightBlue));
                break;
            case 0:
                cardView.setCardBackgroundColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Log.d("TAG", "       Adapter --- onBindViewHolder");
        Log.d("TAG", "       POS = " + position + " CV selected - " + holder.cardView.isSelected());

        //holder.cardView.setSelected(false);
        // holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.taskColorRed));
        // holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,tasks.get(position).getColor()));
        // holder.cardView.setCardBackgroundColor(tasks.get(position).getColor());

        setColorCardView(holder.cardView, position);

        switch (getItemViewType(position)){
            case 0:
                SimpleTask simpleTask = (SimpleTask) tasks.get(position);
                if(simpleTask.getHeadLine().length()==0)
                    holder.stHeadLine.setVisibility(View.GONE);
                else {
                    holder.stHeadLine.setVisibility(View.VISIBLE);
                    holder.stHeadLine.setText(simpleTask.getHeadLine());
                }
                if(simpleTask.getContext().length()==0)
                    holder.stContent.setVisibility(View.GONE);
                else {
                    holder.stContent.setVisibility(View.VISIBLE);
                    holder.stContent.setText(simpleTask.getContext());
                }

                if(tasks.get(position).isRemind()){
                    Log.d("TAG", "       Adapter --- onBindViewHolder TASK " + position + "REMIND IS TRUE" );
                    holder.alarm.setVisibility(View.VISIBLE);
                } else
                    holder.alarm.setVisibility(View.GONE);
                break;
            case 1:
                listTask = (ListTask) tasks.get(position);
             /*        holder.ltFirst.setText(listTask.getUncheckedTask(0));
                    holder.ltSecond.setText(listTask.getUncheckedTask(1));*/
                if(listTask.getHeadLine()!=null && listTask.getHeadLine().length()==0) {
                    holder.listHeadEditText.setVisibility(View.GONE);
                }
                else {
                    holder.listHeadEditText.setVisibility(View.VISIBLE);
                    holder.listHeadEditText.setText(listTask.getHeadLine());
                }


                LayoutInflater inflater = (LayoutInflater)context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);

                holder.layout.removeAllViews();
                for (String s:listTask.getUncheckedTasks()
                        ) {
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBoxDialog);
                    // c.setPressed(true);
                    t.setTypeface(typeFace);
                    t.setText(s);
                    holder.layout.addView(view);
                }
                for (String s:listTask.getCheckedTasks()
                        ) {
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBoxDialog);
                    c.setPressed(true);
                    t.setTypeface(typeFace);
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setText(s);
                    holder.layout.addView(view);
                }

        /*       holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ListTaskActivity.class);
                        intent.putExtra("ListTask", tasks.get(position));
                        context.startActivity(intent);
                    }
                });*/
                if(tasks.get(position).isRemind()){
                    holder.alarm.setVisibility(View.VISIBLE);

                    Log.d("TAG", "       Adapter --- onBindViewHolder TASK " + position + "REMIND IS TRUE" );

                } else
                    holder.alarm.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (tasks.get(position) instanceof SimpleTask)
            return 0;
        if(tasks.get(position) instanceof ListTask) {
            return 1;
        }
        return -1;
    }

    public void dataChanged(List<SuperTask> tasks){
        Log.d("TAG", "       Adapter --- dataChanged");
        this.tasks = tasks;
        selects = new boolean[tasks.size()];
        compareTasks();
        notifyDataSetChanged();
    }

    private void compareTasks(){
        Log.d("TAG", "       Adapter --- compareTasks");
        Comparator<SuperTask> comparator = new Comparator<SuperTask>() {
            @Override
            public int compare(SuperTask superTask, SuperTask t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }
    private void compareSelectionTasks(){
        Log.d("TAG", "       Adapter --- compareTasks");
        Comparator<SuperTask> comparator = new Comparator<SuperTask>() {
            @Override
            public int compare(SuperTask superTask, SuperTask t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }

    private void setRightPosition(){
        Log.d("TAG", "       Adapter --- setRightPosition");
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(tasks.size()-i-1);
        }
    }
}
