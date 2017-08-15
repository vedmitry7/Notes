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
import android.text.format.DateFormat;
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
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.logic.SimpleNote;
import com.example.dmitryvedmed.taskbook.logic.SuperNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.id.headTextView;
import static com.example.dmitryvedmed.taskbook.R.id.taskTextView;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {

    public void setSelectedTasksCounter(int selectedTasksCounter) {
        this.selectedTasksCounter = selectedTasksCounter;
    }

    private List<SuperNote> tasks;
    private List<SuperNote> selectedTasks;
    private Context context;
    private ListNote listNote;
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
    private List<SuperNote> selectedTaskCopy;

    public int getSelectedTasksCounter() {
        return selectedTasksCounter;
    }

    public boolean[] getSelects() {
        return selects;
    }

    public ArrayList<Integer> getSelectedListIds(){
        ArrayList<Integer> list = new ArrayList<>();
        for (SuperNote t:selectedTasks
             ) {
            list.add(t.getId());
        }

        return list;
    }

    public void fillSelectedTasks(ArrayList<Integer> list){
        for (Integer i : list
                ) {
            for (SuperNote t:tasks
                 ) {
                if(t.getId() == i){
                    selectedTasks.add(t);
                }
            }
        }
    }

    public void returnTranslatedTask(String s){
        for (SuperNote st: selectedTaskCopy){
            activity.dbHelper.updateTask(st, s);
        }
        tasks.addAll(selectedTaskCopy);
        selectedTaskCopy.clear();
        dataChanged(tasks);
    }

    public void setSelects(boolean[] selects) {
        this.selects = selects;
        notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void cancelNotification(){

        for (SuperNote note:selectedTasks
             ) {
            Intent intent1 = new Intent(activity.getApplicationContext(), NotifyTaskReceiver.class);
            intent1.setAction(Constants.ACTION_NOTIFICATION);
            PendingIntent sender = PendingIntent.getBroadcast(activity.getApplicationContext(), note.getId(), intent1, 0);
            AlarmManager alarmManager1 = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            alarmManager1.cancel(sender);

            note.setRemind(false);
        }
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

    List<SuperNote> getTasks() {
        return tasks;
    }

    MainRecyclerAdapter(List<SuperNote> tasks, Context context) {
        this.tasks = tasks;

        compareTasks();

        this.context = context;
        selectedTasks = new ArrayList<>();
        activity = (PerfectActivity) context;
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
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

    @Override
    public void onItemDismiss(final int position) {

        selectedTaskCopy.clear();
        selectedTaskCopy.add(tasks.get(position));

        String rem = sharedPreferences.getString(Constants.SWIPE_REMEMBER, "");

        switch (rem){

            case Constants.ARCHIVE:
                if(activity.currentKind == Constants.ARCHIVE){
                    activity.dbHelper.updateTask(tasks.get(position), Constants.UNDEFINED);
                } else {
                    activity.dbHelper.updateTask(tasks.get(position), Constants.ARCHIVE);
                }
                activity.showSnackBar(Constants.ARCHIVE, 1);
                tasks.get(position).setPosition(0);
                tasks.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
                break;
            case Constants.DELETED:
                activity.dbHelper.updateTask(tasks.get(position), Constants.DELETED);
                cancelNotification(tasks.get(position).getId());
                activity.showSnackBar(Constants.DELETED, 1);
                tasks.get(position).setPosition(0);
                tasks.get(position).setRemind(false);
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
                final AlertDialog dialog = mBuilder.create();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
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

                        tasks.get(position).setPosition(0);

                        tasks.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
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

                        tasks.get(position).setPosition(0);
                        tasks.get(position).setRemind(false);
                        //?
                        tasks.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
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
    }

    public void translateTo(String s){
        selectedTaskCopy.addAll(selectedTasks);

        for (SuperNote t:selectedTasks
                ) {
            if(s.equals(Constants.DELETED)){
                t.setRemind(false);
            }
            activity.dbHelper.updateTask(t, s);
        }
        activity.showSnackBar(s, selectedTasksCounter);
        tasks.removeAll(selectedTasks);
        selectedTasks.clear();

        setRightPosition();
        notifyDataSetChanged();
        selectedTasksCounter = 0;
        activity.selectedItemCount(0);
        mode = Mode.NORMAL;
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
        for (SuperNote t : selectedTasks
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
        translateTo(Constants.DELETED);
    }


    public void setColorSelectionTasks(int color){
        for (SuperNote s:selectedTasks
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

    public void updateSelectedTask(){
        List<SuperNote> sa = new ArrayList<>();
        for (SuperNote st:selectedTasks
             ) {
            for (SuperNote t:tasks
                 ) {
                if(t.getId()==st.getId())
                    sa.add(t);
            }
        }
        selectedTasks = sa;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        SuperNote prev = tasks.remove(fromPosition);
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
        private LinearLayout layout, notifInfoContainer;
        private CardView cardView;
        private ImageView alarm;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            stHeadLine = (TextView) itemView.findViewById(headTextView);
            stContent = (TextView) itemView.findViewById(taskTextView);
            alarm = (ImageView) itemView.findViewById(R.id.alarm_ic);

            notifInfo = (TextView) itemView.findViewById(R.id.text_view_notification_info);
            notifInfoContainer = (LinearLayout) itemView.findViewById(R.id.nitif_info_container);

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
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);

        }

        @Override
        public void onItemSelected() {
            if(getAdapterPosition() == -1)
                return;
            if(mode==Mode.NORMAL) {
                cardView.setCardBackgroundColor(Color.LTGRAY);
                wasSelected = true;
                selectedTasks.add(tasks.get(getAdapterPosition()));
            }
        }

        @Override
        public void onItemClear() {
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

            if(position==-1)
                return;

            if(mode == Mode.NORMAL) {

                switch (MainRecyclerAdapter.this.getItemViewType(position)) {
                    case 0:
                        Intent intent = new Intent(context, SimpleNoteActivity.class);
                        intent.putExtra(Constants.TASK, tasks.get(position));
                        intent.putExtra(Constants.KIND, activity.currentKind);
                        context.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    case 1:
                        Intent intent1 = new Intent(context, ListNoteActivity.class);
                        intent1.putExtra(Constants.LIST_TASK, tasks.get(position));
                        intent1.putExtra(Constants.KIND, activity.currentKind);
                        context.startActivity(intent1);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                }
            }
            else if (mode == Mode.SELECTION_MODE) {
                if (selects[position]) {
                    selectedTasks.remove(tasks.get(position));
                    selects[position] = false;
                    selectedTasksCounter--;
                    activity.selectedItemCount(selectedTasksCounter);
                    if(selectedTasksCounter==0) {
                        setSelectionMode(Mode.NORMAL);
                    }
                } else {
                    if(!selectedTasks.contains(tasks.get(position)))
                    selectedTasks.add(tasks.get(position));
                    selectedTasksCounter++;
                    activity.selectedItemCount(selectedTasksCounter);
                    selects[position] = true;
                }

            }
            notifyItemChanged(getAdapterPosition());
        }

        @Override
        public void onItemSelected2() {
            System.out.println("        ItemSELECTED 2");
            if(mode == Mode.NORMAL)
                activity.setSelectionMode();
            int position = getAdapterPosition();
            onClick(new View(context));
        }

        @Override
        public boolean onLongClick(View view) {
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
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_simple_note, parent, false);
                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list_note, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    public List<SuperNote> getSelectedTasks() {
        return selectedTasks;
    }

    private void setColorCardView(CardView cardView, int position){
        if(mode == Mode.SELECTION_MODE) {
            if (selectedTasks.contains(tasks.get(position))) {
                cardView.setCardBackgroundColor(Color.LTGRAY);
            } else {
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
        setColorCardView(holder.cardView, position);

        switch (getItemViewType(position)){
            case 0:
                SimpleNote simpleNote = (SimpleNote) tasks.get(position);
                holder.stHeadLine.setTextSize(textSize);
                holder.stContent.setTextSize(textSize);

                if(simpleNote.getHeadLine().length()==0)
                    holder.stHeadLine.setVisibility(View.GONE);
                else {
                    holder.stHeadLine.setVisibility(View.VISIBLE);
                    holder.stHeadLine.setText(simpleNote.getHeadLine());
                }
                if(simpleNote.getContext().length()==0)
                    holder.stContent.setVisibility(View.GONE);
                else {
                    holder.stContent.setVisibility(View.VISIBLE);
                    holder.stContent.setText(simpleNote.getContext());
                }

                if(tasks.get(position).isRemind()){
                    holder.alarm.setVisibility(View.VISIBLE);
                } else
                    holder.alarm.setVisibility(View.GONE);

                if(activity.isNotification_on()){
                    holder.notifInfoContainer.setVisibility(View.VISIBLE);
                    if(holder.notifInfo!=null) {

                        SuperNote task = tasks.get(position);
                        String dateString = DateFormat.format("dd/MM/yyyy", new Date(task.getReminderTime())).toString();
                        String timeString = DateFormat.format("H:mm", new Date(task.getReminderTime())).toString();
                        String repeating;

                        if(task.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY){
                            repeating = activity.getResources().getString(R.string.every_day);
                        } else if(task.getRepeatingPeriod() == Constants.PERIOD_WEEK){
                            repeating = activity.getResources().getString(R.string.every_week);
                        } else if(task.getRepeatingPeriod() == Constants.PERIOD_MONTH){
                            repeating = activity.getResources().getString(R.string.every_month);
                        } else {
                            repeating = "None";
                        }
                        holder.notifInfo.setText(activity.getResources().getString(R.string.date) + " : " + dateString + "\r\n" +
                                "Время : " + timeString+ "\r\n" + "Повтор : " + repeating);
                    }
                } else {
                    holder.notifInfoContainer.setVisibility(View.GONE);
                }

                break;

            case 1:
                listNote = (ListNote) tasks.get(position);
                holder.listHeadEditText.setTextSize(textSize);
                if(listNote.getHeadLine()!=null && listNote.getHeadLine().length()==0) {
                    holder.listHeadEditText.setVisibility(View.GONE);
                }
                else {
                    holder.listHeadEditText.setVisibility(View.VISIBLE);
                    holder.listHeadEditText.setText(listNote.getHeadLine());
                }

                LayoutInflater inflater = (LayoutInflater)context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);

                holder.layout.removeAllViews();

                int count = 0;
                String s = null;
                for (int i = 0; i < 5; i++) {
                    if(listNote.getUncheckedTasks().size()-1 < i){
                        break;
                    }
                    count++;
                    s = listNote.getUncheckedTasks().get(i);
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    t.setMaxLines(2);
                    t.setTextSize(textSize);
                    t.setEllipsize(TextUtils.TruncateAt.END);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBoxDialog);
                    t.setTypeface(typeFace);
                    t.setText(s);
                    holder.layout.addView(view);
                }
                for (int i = 0; i < 5 - count ; i++) {
                    if(listNote.getCheckedTasks().size()-1 < i){
                        break;
                    }
                    s = listNote.getCheckedTasks().get(i);
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
                if(tasks.get(position).isRemind()){
                    holder.alarm.setVisibility(View.VISIBLE);
                } else
                    holder.alarm.setVisibility(View.GONE);

                break;
        }

        if(activity.isNotification_on()){
            holder.notifInfoContainer.setVisibility(View.VISIBLE);
            if(holder.notifInfo!=null) {

                SuperNote task = tasks.get(position);

                String dateString = DateFormat.format("dd.MM.yyyy", new Date(task.getReminderTime())).toString();
                String timeString = DateFormat.format("H:mm", new Date(task.getReminderTime())).toString();
                String repeating;

                if(task.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY){
                    repeating = activity.getResources().getString(R.string.every_day);
                } else if(task.getRepeatingPeriod() == Constants.PERIOD_WEEK){
                    repeating = activity.getResources().getString(R.string.every_week);
                } else if(task.getRepeatingPeriod() == Constants.PERIOD_MONTH){
                    repeating = activity.getResources().getString(R.string.every_month);
                } else {
                    repeating = "";
                }

                holder.notifInfo.setText(dateString + " " + timeString+ " " + repeating);
            }
        } else {
            holder.notifInfoContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (tasks.get(position) instanceof SimpleNote)
            return 0;
        if(tasks.get(position) instanceof ListNote) {
            return 1;
        }
        return -1;
    }

    public void dataChanged(List<SuperNote> tasks){
        this.tasks = tasks;
        if(!activity.is_in_action_mode()){
            selects = new boolean[tasks.size()];
        }
        textSize = sharedPreferences.getInt("cardFontSize", 16);
        compareTasks();
        selectedTaskCopy = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void compareTasks(){
        Comparator<SuperNote> comparator = new Comparator<SuperNote>() {
            @Override
            public int compare(SuperNote superTask, SuperNote t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }
    private void compareSelectionTasks(){
        Comparator<SuperNote> comparator = new Comparator<SuperNote>() {
            @Override
            public int compare(SuperNote superTask, SuperNote t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(tasks, comparator);
    }

    private void setRightPosition(){
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(tasks.size()-i-1);
        }
    }
}
