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

    public void setSelectedTasksCounter(int selectedTasksCounter) {
        this.selectedTasksCounter = selectedTasksCounter;
    }

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
    private List<SuperTask> selectedTaskCopy;

    public boolean[] getSelects() {
        return selects;
    }

    public ArrayList<Integer> getSelectedListIds(){
        ArrayList<Integer> list = new ArrayList<>();
        for (SuperTask t:selectedTasks
             ) {
            list.add(t.getId());
        }

        return list;
    }

    public void setSelectedTaskCopy(List<SuperTask> selectedTaskCopy) {
        this.selectedTaskCopy = selectedTaskCopy;
        Log.d("TAG", "       Adapter ---  COOOOOOOOOOOPYYYY SIZE = " + selectedTasks.size());
    }

    public void fillSelectedTasks(ArrayList<Integer> list){
        for (Integer i : list
                ) {
            for (SuperTask t:tasks
                 ) {
                if(t.getId() == i){
                    selectedTasks.add(t);
                }
            }
        }
        Log.d("TAG", "       Adapter ---  fillSelectedTasks S = " + selectedTasks.size());

    }

    public void returnTranslatedTask(String s){
        Log.d("TAG", "       Adapter ---  RETUUUUUUURN COPY SIZE start = " + selectedTaskCopy.size());
        for (SuperTask st: selectedTaskCopy){
            activity.dbHelper.updateTask(st, s);
        }
        Log.d("TAG", "       Adapter ---  RETUUUUUUURN TASKS SIZE start = " + tasks.size());
        tasks.addAll(selectedTaskCopy);
        Log.d("TAG", "       Adapter ---  RETUUUUUUURN TASKS SIZE finish = " + tasks.size());
        selectedTaskCopy.clear();
        Log.d("TAG", "       Adapter ---  RETUUUUUUURN COPY finish = " + selectedTaskCopy.size());

        dataChanged(tasks);
    }

    public void setSelects(boolean[] selects) {
            Log.d("TAG", "       Adapter --- setSelects(boolean[] selects");
        this.selects = selects;
        notifyDataSetChanged();
        notifyDataSetChanged();
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
        Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");
        activity = (PerfectActivity) context;
      //  Log.d("TAG", "       Adapter, tasksSize = " + tasks.size());
        textView = new TextView(context);
        textView.setText("1234we5r");
        typeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
        boldTypeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
        mode = Mode.NORMAL;

        selects = new boolean[tasks.size()];

        selectedTaskCopy = new ArrayList<>();

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

        selectedTaskCopy.clear();
        selectedTaskCopy.add(tasks.get(position));

        String rem = sharedPreferences.getString(Constants.SWIPE_REMEMBER, "");
       // Log.d("TAG", "       Adapter --- onItemDismiss, position = " + position + " " + rem);

        switch (rem){

            case Constants.ARCHIVE:
                if(activity.currentKind == Constants.ARCHIVE){
                    activity.dbHelper.updateTask(tasks.get(position), Constants.UNDEFINED);
                } else {
                    activity.dbHelper.updateTask(tasks.get(position), Constants.ARCHIVE);
                }
                activity.showSnackBar(Constants.ARCHIVE, 1);
                tasks.get(position).setPosition(0);                 //?
                tasks.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
                break;
            case Constants.DELETED:
                activity.dbHelper.updateTask(tasks.get(position), Constants.DELETED);
                cancelNotification(tasks.get(position).getId());
                activity.showSnackBar(Constants.DELETED, 1);
                tasks.get(position).setPosition(0);                 //?
                tasks.get(position).setRemind(false);                 //?
                tasks.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
                break;
            case "" :
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = activity.getLayoutInflater().inflate(R.layout.dialog_question_layout, null);
                mBuilder.setCancelable(true);
                RelativeLayout archive = (RelativeLayout) mView.findViewById(R.id.actionArchive);
                RelativeLayout delete = (RelativeLayout) mView.findViewById(R.id.actionDelete);
                final CheckBox remember = (CheckBox) mView.findViewById(R.id.remember);
                if(activity.currentKind.equals(Constants.ARCHIVE)){
                    TextView textView = (TextView) mView.findViewById(R.id.archiveTextView);
                    textView.setText(R.string.unarchive);
                }
                mBuilder.setView(mView);
                //mBuilder.setView(R.layout.dialog_question_layout);
                final AlertDialog dialog = mBuilder.create();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                   //     Log.d("TAG", "       Adapter --- CANCEL LISTENER ");
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
                        activity.showSnackBar(Constants.ARCHIVE, 1);
                        dialog.dismiss();

                        tasks.get(position).setPosition(0);//?

                        tasks.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
                            Log.d("TAG", "       Adapter ---                                CHECKED ");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.SWIPE_REMEMBER, Constants.ARCHIVE);
                            editor.commit();
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.dbHelper.updateTask(tasks.get(position), Constants.DELETED);
                        cancelNotification(tasks.get(position).getId());
                        dialog.dismiss();
                        activity.showSnackBar(Constants.DELETED, 1);

                        tasks.get(position).setPosition(0);                 //?
                        tasks.get(position).setRemind(false);
                        //?
                        tasks.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
                            Log.d("TAG", "       Adapter ---                                CHECKED ");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.SWIPE_REMEMBER, Constants.DELETED);
                            editor.commit();
                        }
                    }
                });
                dialog.show();
                break;
        }


    }

    @Override
    public void onItemSelected() {
      //  System.out.println(" onItem SELECTED ");

    }

    public void translateTo(String s){
        //    Log.d("TAG", "           adapter translateTo " + s);
        selectedTaskCopy.addAll(selectedTasks);
        Log.d("TAG", "       Adapter --- !                      COOOOOOOOOOOOPYYYY111111 " + selectedTaskCopy.size());
        for (SuperTask t:selectedTasks
                ) {
            //        Log.d("TAG", t.getId() +  "       translateTo " + s);
            activity.dbHelper.updateTask(t, s);
        }
        activity.showSnackBar(s, selectedTasksCounter);
        tasks.removeAll(selectedTasks);
        selectedTasks.clear();
        Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");

        setRightPosition();
        notifyDataSetChanged();
        selectedTasksCounter = 0;
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selects = new boolean[tasks.size()];
        Log.d("TAG", "       Adapter --- !                      COOOOOOOOOOOOPYYYY2222222222 " + selectedTaskCopy.size());
    }

    public void cancelSelection() {
        selectedTasks.clear();
        Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");

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
        Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");
        selectedTasks.clear();
        setRightPosition();
        notifyDataSetChanged();
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
    }


    public void deleteSection(){
        selectedTasks.addAll(tasks);
        translateTo(Constants.DELETED);
    }


    public void setColorSelectionTasks(int color){
        Log.d("TAG", "!!!!!!!!!             AAAAAA SET COLOR sel tasks size = " + selectedTasks.size());
        Log.d("TAG", "!!!!!!!!!        after update     AAAAAA SET COLOR sel tasks size = " + selectedTasks.size());

        for (SuperTask s:tasks
             ) {
            Log.d("TAG",s.getId() +  " code = " + s.hashCode());
        }
        Log.d("TAG","___________________________________________");
        for (SuperTask s:selectedTasks
                ) {
            Log.d("TAG", s.getId() +  " code = " + s.hashCode());
            s.setColor(color);
        }
        notifyDataSetChanged();
        selectedTasks.clear();
        Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");

        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedTasksCounter = 0;
        selects = new boolean[tasks.size()];
    }

    public void updateSelectedTask(){
        List<SuperTask> sa = new ArrayList<>();
        for (SuperTask st:selectedTasks
             ) {
            for (SuperTask t:tasks
                 ) {
                if(t.getId()==st.getId())
                    sa.add(t);
            }
        }
        selectedTasks = sa;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    //    Log.d("TAG", "       Adapter --- onItemMove, FROM - " + fromPosition + ", TO - " + toPosition);
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
        private TextView stHeadLine, stContent, listHeadEditText, notifInfo;
        private LinearLayout layout;
        private CardView cardView;
        private ImageView alarm;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            stHeadLine = (TextView) itemView.findViewById(headTextView);
            stContent = (TextView) itemView.findViewById(taskTextView);
            alarm = (ImageView) itemView.findViewById(R.id.alarm_ic);
            notifInfo = (TextView) itemView.findViewById(R.id.text_view_notification_info);
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
        //    Log.d("TAG", "       Adapter --- onItemSelected POSITION " + getAdapterPosition());
            if(getAdapterPosition() == -1)
                return;
            if(mode==Mode.NORMAL) {
          //      Log.d("TAG", "                                              COLOR CHANGE  LT GRAY ON ITEM SEl");
                cardView.setCardBackgroundColor(Color.LTGRAY);
                wasSelected = true;
                selectedTasks.add(tasks.get(getAdapterPosition()));
            }
        }

        @Override
        public void onItemClear() {
         //   Log.d("TAG", "       Adapter --- onItemClear");
            if(mode != Mode.SELECTION_MODE) {
                wasSelected = false;
                selectedTasks.clear();
                Log.d("TAG", "       Adapter --- !!!!!!!!!!!!!!!!!!!!!!!!!!!!selectedTasks.clear()");

                if (getAdapterPosition() != -1)
                    setColorCardView(cardView, getAdapterPosition());
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
         //   Log.d("TAG", "       Adapter --- onClick " + position);
         //   Log.d("TAG", "       Adapter --- onClick " + view.toString());
            Log.d("TAG", "                                          start             ON Click " + selectedTasks.size());

            if(position==-1)
                return;

            if(mode == Mode.NORMAL) {
        //        Log.d("TAG", "       Adapter --- MODE NORMAL " + MainRecyclerAdapter.this.getItemViewType(position) );

                switch (MainRecyclerAdapter.this.getItemViewType(position)) {
                    case 0:
                        Intent intent = new Intent(context, SimpleTaskActivity.class);
                        intent.putExtra(Constants.TASK, (Serializable) tasks.get(position));
                        intent.putExtra(Constants.KIND, activity.currentKind);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    case 1:
                        Intent intent1 = new Intent(context, ListTaskActivity.class);
                        intent1.putExtra(Constants.LIST_TASK, tasks.get(position));
                        intent1.putExtra(Constants.KIND, activity.currentKind);
                        context.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                }
            }
            else if (mode == Mode.SELECTION_MODE) {
           //     Log.d("TAG", "       Adapter --- MODE NE NORMAL " );
                if (selects[position]) {
              //      Log.d("TAG", "       SELECTED POSITION " + position +" теперь False" );
                    // cardView.setCardBackgroundColor(Color.WHITE);
                    selectedTasks.remove(tasks.get(position));

                    // cardView.setSelected(false);
                    selects[position] = false;
                    selectedTasksCounter--;
                    activity.selectedItemCount(selectedTasksCounter);
                    if(selectedTasksCounter==0) {
                        setSelectionMode(Mode.NORMAL);
                    }
               //     Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                } else {
                   // cardView.setCardBackgroundColor(Color.LTGRAY);
                 //   cardView.setSelected(true);
              //      Log.d("TAG", "       SELECTED POSITION " + position +" теперь True" );
                    if(!selectedTasks.contains(tasks.get(position)))
                    selectedTasks.add(tasks.get(position));
                    selectedTasksCounter++;
                    activity.selectedItemCount(selectedTasksCounter);
                    selects[position] = true;
             //       Log.d("TAG", "       selectedTasksCounter " + selectedTasksCounter);
                }
             //   Log.d("TAG", "       Adapter --- sel. size" + selectedTasks.size());
            }
            notifyItemChanged(getAdapterPosition());
        //    Log.d("TAG", "                                             end          ON Click " + selectedTasks.size());
        }

        @Override
        public void onItemSelected2() {
            System.out.println("        ItemSELECTED 2");
            if(mode == Mode.NORMAL)
                activity.setSelectionMode();
            int position = getAdapterPosition();
      //      Log.d("TAG", "       SELECTED POSITION " + position + " теперь True Mode normal - " + (mode==Mode.NORMAL));

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
        //    Log.d("TAG", "      LOOOOONG CLICK " );
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
     //   Log.d("TAG", "       Adapter --- onCreateViewHolder");
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType) {
            case 0:
                //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_simple_task, parent,false);
                View view ;

                if(!activity.isNotification_on()){
                    Log.d("TAG", "       Adapter --- onCreateViewHolder       WITHOUT INFO");
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_simple_task, parent,false);}
                else {
                    Log.d("TAG", "       Adapter --- onCreateViewHolder       WITH INFO WITH INFO WITH INFO");
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_simple_task_with_date, parent, false);
                }

                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list_task, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    public List<SuperTask> getSelectedTasks() {
        return selectedTasks;
    }

    private void setColorCardView(CardView cardView, int position){
        Log.d("TAG", "       setColorCardView");
 /*       for (SuperTask s:selectedTasks
                ) {
            Log.d("TAG", "  sel task  id = " + s.hashCode() +" "+ s.getId());

        }
        for (SuperTask s:tasks
                ) {
            Log.d("TAG", "  sel task  id = " + s.hashCode() +" "+ s.getId());
        }
*/
        if(mode == Mode.SELECTION_MODE) {
            Log.d("TAG", "                          on selection mode");

      //      Log.d("TAG", "       Adapter --- setColorCardView SELECTION MODE ON ");
     //       Log.d("TAG", "       SelectedTasks Size = " + selectedTasks.size() + " position = " + position);

       /*     int id = tasks.get(position).getId();
            boolean b = false;
            for (SuperTask s:selectedTasks
                 ) {
                if(s.getId() == id){
                    b = true;
                    continue;
                }
            }

            if(b){*/
            if (selectedTasks.contains(tasks.get(position))) {
                Log.d("TAG", "       SelectedTasks contains = " + position);
       //         Log.d("TAG", "                                              COLOR CHANGE  LT GRAY ON BIND VH");

                cardView.setCardBackgroundColor(Color.LTGRAY);

            } else {
       //         Log.d("TAG", "       Adapter --- NOT SELECTION");
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
         //               Log.d("TAG", "                                              COLOR CHANGE WHITE!!!!!!!!!  ON BIND VH");
                        cardView.setCardBackgroundColor(Color.WHITE);
                        break;
                }
            }
            return;
        }
        if (position >= tasks.size())
            return;
    //    Log.d("TAG", "                          on normal mode");
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
   //             Log.d("TAG", "                                              COLOR CHANGE WHITE not sel mode  ON BIND VH");
                cardView.setCardBackgroundColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Log.d("TAG", "       Adapter --- onBindViewHolder");
     //   Log.d("TAG", "       POS = " + position + " CV selected - " + holder.cardView.isSelected());
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
        //           Log.d("TAG", "       Adapter --- onBindViewHolder TASK " + position + "REMIND IS TRUE" );
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
            //        Log.d("TAG", "       Adapter --- onBindViewHolder TASK " + position + "REMIND IS TRUE" );
                } else
                    holder.alarm.setVisibility(View.GONE);
                break;
            default:
                if(activity.isNotification_on() && holder.notifInfo!=null)
                    holder.notifInfo.setText("next notif - " + String.valueOf(tasks.get(position).getReminderTime()));
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
        if(!activity.is_in_action_mode()){
            selects = new boolean[tasks.size()];
            Log.d("TAG", "       Adapter ---                                                selects = new boolean[tasks.size()];");
        } else             Log.d("TAG", "       Adapter ---                  ne obnuliaem");

        textSize = sharedPreferences.getInt("cardFontSize", 16);
        compareTasks();
        selectedTaskCopy = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void compareTasks(){
    //    Log.d("TAG", "       Adapter --- compareTasks");
        Comparator<SuperTask> comparator = new Comparator<SuperTask>() {
            @Override
            public int compare(SuperTask superTask, SuperTask t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }
    private void compareSelectionTasks(){
    //    Log.d("TAG", "       Adapter --- compareTasks");
        Comparator<SuperTask> comparator = new Comparator<SuperTask>() {
            @Override
            public int compare(SuperTask superTask, SuperTask t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }

    private void setRightPosition(){
    //    Log.d("TAG", "       Adapter --- setRightPosition");
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(tasks.size()-i-1);
        }
    }
}
