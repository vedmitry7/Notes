package com.example.dmitryvedmed.taskbook.ui;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private PerfectActivity activity;
    boolean wasSelected;
    private Mode mode;
    private int selectedTasksCounter;
    private boolean[] selects;
    private SharedPreferences sharedPreferences;
    private int textSize;
    public int getSelectedTasksCounter() {
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
        activity = (PerfectActivity) context;
        Log.d("TAG", "       Adapter, tasksSize = " + tasks.size());
        textView = new TextView(context);
        textView.setText("1234we5r");
        typeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
        boldTypeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
        mode = Mode.NORMAL;
        selects = new boolean[tasks.size()];

        sharedPreferences = activity.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);

        textSize = sharedPreferences.getInt("cardFontSize", 16);
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
    public void onItemDismiss(final int position) {
        Log.d("TAG", "       Adapter --- onItemDismiss, position = " + position);


        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View mViewe = activity.getLayoutInflater().inflate(R.layout.dialog_question_layout, null);
        mBuilder.setCancelable(true);
        RelativeLayout archive = (RelativeLayout) mViewe.findViewById(R.id.actionArchive);
        RelativeLayout delete = (RelativeLayout) mViewe.findViewById(R.id.actionDelete);
        CheckBox remember = (CheckBox) mViewe.findViewById(R.id.remember);
        if(activity.currentKind==Constants.ARCHIVE){
            TextView textView = (TextView) mViewe.findViewById(R.id.archiveTextView);
            textView.setText("Разархивировать");
        }


        mBuilder.setView(mViewe);
        final AlertDialog dialog = mBuilder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TAG", "       Adapter --- CANCEL LISTENER ");
                notifyDataSetChanged();
            }
        });
        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity.currentKind == Constants.ARCHIVE){
                    activity.dbHelper.updateTask(tasks.get(position), Constants.UNDEFINED);
                } else {
                    activity.dbHelper.updateTask(tasks.get(position), Constants.ARCHIVE);
                }
                activity.showSnackBar(1);
                dialog.dismiss();

                tasks.get(position).setPosition(0);                 //?

                tasks.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.dbHelper.updateTask(tasks.get(position), Constants.DELETED);
                cancelNotification(tasks.get(position).getId());
                dialog.dismiss();
                activity.showSnackBar(1);

                tasks.get(position).setPosition(0);                 //?
                tasks.get(position).setRemind(false);                 //?

                tasks.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected() {
        System.out.println(" onItem SELECTED ");

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
            activity.dbHelper.deleteTask(t);
        }
        tasks.removeAll(selectedTasks);
        selectedTasks.clear();
        setRightPosition();
        notifyDataSetChanged();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
    }

    public void deleteSection(){
        selectedTasks.addAll(tasks);
        deleteSelectedTasks();
    }


    public void translateTo(String s){
        Log.d("TAG", "           adapter translateTo " + s);
        for (SuperTask t:selectedTasks
                ) {
            Log.d("TAG", t.getId() +  "       translateTo " + s);
            activity.dbHelper.updateTask(t, s);
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

        boolean fp = selects[fromPosition];
        selects[fromPosition] = selects[toPosition];
        selects[toPosition] = fp;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private TextView stHeadLine, stContent, listHeadEditText;
        private LinearLayout layout;
        private CardView cardView;
        private ImageView alarm;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            Log.d("TAG", "       Adapter --- RECYCLER VIEW Holder");
            stHeadLine = (TextView) itemView.findViewById(headTextView);
            stContent = (TextView) itemView.findViewById(taskTextView);
            alarm = (ImageView) itemView.findViewById(R.id.alarm_ic);
            alarm.setVisibility(View.GONE);


            if(stContent!=null) {
                stHeadLine.setTypeface(boldTypeFace);
                stContent.setTypeface(typeFace);
            }


            listHeadEditText = (TextView) itemView.findViewById(R.id.mainRecListItemHead);
            if(listHeadEditText!=null){
                listHeadEditText.setTextSize(sharedPreferences.getInt("cardFontSize", 10));
            }

            layout = (LinearLayout) itemView.findViewById(R.id.card_view_list_layout);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            //cardView.setOnLongClickListener((DrawerTestActivity)context);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);

        }

        @Override
        public void onItemSelected() {
            Log.d("TAG", "       Adapter --- onItemSelected POSITION " + getAdapterPosition());
            if(getAdapterPosition() == -1)
                return;
            if(mode==Mode.NORMAL) {
                Log.d("TAG", "                                              COLOR CHANGE  LT GRAY ON ITEM SEl");
                cardView.setCardBackgroundColor(Color.LTGRAY);
                wasSelected = true;
                selectedTasks.add(tasks.get(getAdapterPosition()));
            }
        }

        @Override
        public void onItemClear() {
            Log.d("TAG", "       Adapter --- onItemClear");
            if(mode != Mode.SELECTION_MODE) {
                wasSelected = false;
                selectedTasks.clear();
                if (getAdapterPosition() != -1)
                    setColorCardView(cardView, getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("TAG", "       Adapter --- onClick " + position);
            Log.d("TAG", "       Adapter --- onClick " + view.toString());
            Log.d("TAG", "                                          start             ON Click " + selectedTasks.size());

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
                    if(selectedTasksCounter==0) {
                        setSelectionMode(Mode.NORMAL);
                    }
                    Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                } else {
                   // cardView.setCardBackgroundColor(Color.LTGRAY);
                 //   cardView.setSelected(true);
                    Log.d("TAG", "       SELECTED POSITION " + position +" теперь True" );
                    if(!selectedTasks.contains(tasks.get(position)))
                    selectedTasks.add(tasks.get(position));
                    selectedTasksCounter++;
                    activity.selectedItemCount(selectedTasksCounter);
                    selects[position] = true;
                    Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                }
                Log.d("TAG", "       Adapter --- sel. size" + selectedTasks.size());
            }
            notifyItemChanged(getAdapterPosition());
            Log.d("TAG", "                                             end          ON Click " + selectedTasks.size());
        }

        @Override
        public void onItemSelected2() {
            System.out.println("        ItemSELECTED 2");
            if(mode == Mode.NORMAL)
                activity.setSelectionMode();
            int position = getAdapterPosition();
            Log.d("TAG", "       SELECTED POSITION " + position + " теперь True Mode normal - " + (mode==Mode.NORMAL));

            onClick(new View(context));

            /* if(getAdapterPosition()!=-1) {
                Log.d("TAG", "       EAHHH  work");
                if(selects[position]){
                    selectedTasksCounter--;
                    selects[position] = false;
                    selectedTasks.remove(tasks.get(position));
                    onClick(new View(context));
                } else{
                    selectedTasksCounter++;
                    selects[position] = true;
                }
                activity.selectedItemCount(selectedTasksCounter);
                notifyItemChanged(position);
            }*/
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d("TAG", "      LOOOOONG CLICK " );
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
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
            Log.d("TAG", "                          on selection mode");

            Log.d("TAG", "       Adapter --- setColorCardView SELECTION MODE ON ");
            Log.d("TAG", "       SelectedTasks Size = " + selectedTasks.size() + " position = " + position);

            if (selectedTasks.contains(tasks.get(position))) {
                Log.d("TAG", "       SelectedTasks contains = " + position);
                Log.d("TAG", "                                              COLOR CHANGE  LT GRAY ON BIND VH");

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
                        Log.d("TAG", "                                              COLOR CHANGE WHITE!!!!!!!!!  ON BIND VH");
                        cardView.setCardBackgroundColor(Color.WHITE);
                        break;
                }
            }
            return;
        }
        if (position >= tasks.size())
            return;
        Log.d("TAG", "                          on normal mode");
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
                Log.d("TAG", "                                              COLOR CHANGE WHITE not sel mode  ON BIND VH");
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
                holder.stHeadLine.setTextSize(textSize);
                holder.stContent.setTextSize(textSize);

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

                holder.listHeadEditText.setTextSize(textSize);
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

                int count = 0;
                String s = null;
                for (int i = 0; i < 5; i++) {
                    if(listTask.getUncheckedTasks().size()-1 < i){
                        break;
                    }
                    count++;
                    s = listTask.getUncheckedTasks().get(i);
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    t.setMaxLines(2);
                    t.setTextSize(textSize);
                    t.setEllipsize(TextUtils.TruncateAt.END);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBoxDialog);
                    // c.setPressed(true);
                    t.setTypeface(typeFace);
                    t.setText(s);
                    holder.layout.addView(view);
                }
                for (int i = 0; i < 5 - count ; i++) {
                    if(listTask.getCheckedTasks().size()-1 < i){
                        break;
                    }
                    s = listTask.getCheckedTasks().get(i);
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    t.setMaxLines(2);
                    t.setTextSize(textSize);
                    t.setEllipsize(TextUtils.TruncateAt.END);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBoxDialog);
                    c.setSelected(true);
                    t.setTypeface(typeFace);
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setText(s);
                    holder.layout.addView(view);

                }
         /*       for (String s:listTask.getUncheckedTasks()
                        ) {
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    t.setMaxLines(2);
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
                */
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
        textSize = sharedPreferences.getInt("cardFontSize", 16);
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
