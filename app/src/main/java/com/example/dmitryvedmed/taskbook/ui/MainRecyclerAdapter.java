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
import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.id.headTextView;
import static com.example.dmitryvedmed.taskbook.R.id.taskTextView;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {

    public void setSelectedNotesCounter(int selectedNotesCounter) {
        this.selectedNotesCounter = selectedNotesCounter;
    }

    private List<SuperNote> notes;
    private List<SuperNote> superNotes;
    private Context mContext;
    private ListNote listNote;
    private TextView textView;
    private Typeface typeFace;
    private Typeface boldTypeFace ;
    private PerfectActivity mActivity;
    boolean wasSelected;
    private Mode mode;
    private int selectedNotesCounter;
    private boolean[] selects;
    private SharedPreferences mSharedPreferences;
    private int textSize;
    private List<SuperNote> selectedNotesCopy;

    public int getSelectedNotesCounter() {
        return selectedNotesCounter;
    }

    public boolean[] getSelects() {
        return selects;
    }

    public ArrayList<Integer> getSelectedListIds(){
        ArrayList<Integer> list = new ArrayList<>();
        for (SuperNote t: superNotes
             ) {
            list.add(t.getId());
        }

        return list;
    }

    public void fillSelectedTasks(ArrayList<Integer> list){
        for (Integer i : list
                ) {
            for (SuperNote t: notes
                 ) {
                if(t.getId() == i){
                    superNotes.add(t);
                }
            }
        }
    }

    public void returnTranslatedTask(String s){
        for (SuperNote st: selectedNotesCopy){
            mActivity.sDbHelper.updateTask(st, s);
        }
        notes.addAll(selectedNotesCopy);
        selectedNotesCopy.clear();
        dataChanged(notes);
    }

    public void setSelects(boolean[] selects) {
        this.selects = selects;
        notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void cancelNotification(){

        for (SuperNote note: superNotes
             ) {
            Intent intent1 = new Intent(mActivity.getApplicationContext(), NotifyTaskReceiver.class);
            intent1.setAction(Constants.ACTION_NOTIFICATION);
            PendingIntent sender = PendingIntent.getBroadcast(mActivity.getApplicationContext(), note.getId(), intent1, 0);
            AlarmManager alarmManager1 = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
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

    List<SuperNote> getNotes() {
        return notes;
    }

    MainRecyclerAdapter(List<SuperNote> notes, Context mContext) {
        this.notes = notes;

        compareTasks();

        this.mContext = mContext;
        superNotes = new ArrayList<>();
        mActivity = (PerfectActivity) mContext;
        textView = new TextView(mContext);
        textView.setText("1234we5r");
        typeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Roboto-Regular.ttf");
        boldTypeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Roboto-Bold.ttf");
        mode = Mode.NORMAL;

        selects = new boolean[notes.size()];

        selectedNotesCopy = new ArrayList<>();

        mSharedPreferences = mActivity.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);

        textSize = mSharedPreferences.getInt("cardFontSize", 16);
    }

    private void cancelNotification(int requestCode){

        Intent intent = new Intent(mContext, NotifyTaskReceiver.class);
        intent.setAction("TASK_NOTIFICATION");
        PendingIntent sender = PendingIntent.getBroadcast(mContext, requestCode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

    @Override
    public void onItemDismiss(final int position) {

        selectedNotesCopy.clear();
        selectedNotesCopy.add(notes.get(position));

        String rem = mSharedPreferences.getString(Constants.SWIPE_REMEMBER, "");

        switch (rem){

            case Constants.ARCHIVE:
                if(mActivity.currentKind == Constants.ARCHIVE){
                    mActivity.sDbHelper.updateTask(notes.get(position), Constants.UNDEFINED);
                } else {
                    mActivity.sDbHelper.updateTask(notes.get(position), Constants.ARCHIVE);
                }
                mActivity.showSnackBar(Constants.ARCHIVE, 1);
                notes.get(position).setPosition(0);
                notes.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
                break;
            case Constants.DELETED:
                mActivity.sDbHelper.updateTask(notes.get(position), Constants.DELETED);
                cancelNotification(notes.get(position).getId());
                mActivity.showSnackBar(Constants.DELETED, 1);
                notes.get(position).setPosition(0);
                notes.get(position).setRemind(false);
                notes.remove(position);
                notifyItemRemoved(position);
                setRightPosition();
                break;
            case "" :
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = mActivity.getLayoutInflater().inflate(R.layout.dialog_question_layout, null);
                mBuilder.setCancelable(true);
                RelativeLayout archive = (RelativeLayout) mView.findViewById(R.id.actionArchive);
                RelativeLayout delete = (RelativeLayout) mView.findViewById(R.id.actionDelete);
                final CheckBox remember = (CheckBox) mView.findViewById(R.id.remember);
                if(mActivity.currentKind.equals(Constants.ARCHIVE)){
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
                        if(mActivity.currentKind == Constants.ARCHIVE){
                            mActivity.sDbHelper.updateTask(notes.get(position), Constants.UNDEFINED);
                        } else {
                            mActivity.sDbHelper.updateTask(notes.get(position), Constants.ARCHIVE);
                        }
                        mActivity.showSnackBar(Constants.ARCHIVE, 1);
                        dialog.dismiss();

                        notes.get(position).setPosition(0);

                        notes.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString(Constants.SWIPE_REMEMBER, Constants.ARCHIVE);
                            editor.commit();
                        }
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.sDbHelper.updateTask(notes.get(position), Constants.DELETED);
                        cancelNotification(notes.get(position).getId());
                        dialog.dismiss();
                        mActivity.showSnackBar(Constants.DELETED, 1);

                        notes.get(position).setPosition(0);
                        notes.get(position).setRemind(false);
                        //?
                        notes.remove(position);
                        notifyItemRemoved(position);
                        setRightPosition();
                        if(remember.isChecked()){
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
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
        selectedNotesCopy.addAll(superNotes);

        for (SuperNote t: superNotes
                ) {
            if(s.equals(Constants.DELETED)){
                t.setRemind(false);
            }
            mActivity.sDbHelper.updateTask(t, s);
        }
        mActivity.showSnackBar(s, selectedNotesCounter);
        notes.removeAll(superNotes);
        superNotes.clear();

        setRightPosition();
        notifyDataSetChanged();
        selectedNotesCounter = 0;
        mActivity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selects = new boolean[notes.size()];
    }

    public void cancelSelection() {
        superNotes.clear();

        mActivity.selectedItemCount(0);
        mode = Mode.NORMAL;
        notifyDataSetChanged();
        selectedNotesCounter = 0;
        selects = new boolean[notes.size()];
    }

    public void deleteSelectedTasksForever() {
        compareSelectionTasks();
        for (SuperNote t : superNotes
                ) {
            mActivity.sDbHelper.deleteTask(t);
        }
        notes.removeAll(superNotes);
        superNotes.clear();
        setRightPosition();
        notifyDataSetChanged();
        mActivity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedNotesCounter = 0;
    }


    public void deleteSection(){
        superNotes.addAll(notes);
        translateTo(Constants.DELETED);
    }


    public void setColorSelectionTasks(int color){
        for (SuperNote s: superNotes
                ) {
            s.setColor(color);
        }
        notifyDataSetChanged();
        superNotes.clear();

        mActivity.selectedItemCount(0);
        mode = Mode.NORMAL;
        selectedNotesCounter = 0;
        selects = new boolean[notes.size()];
    }

    public void updateSelectedTask(){
        List<SuperNote> sa = new ArrayList<>();
        for (SuperNote st: superNotes
             ) {
            for (SuperNote t: notes
                 ) {
                if(t.getId()==st.getId())
                    sa.add(t);
            }
        }
        superNotes = sa;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        SuperNote prev = notes.remove(fromPosition);
        notes.add(toPosition, prev);
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
                listHeadEditText.setTextSize(mSharedPreferences.getInt("cardFontSize", 10));
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
                superNotes.add(notes.get(getAdapterPosition()));
            }
        }

        @Override
        public void onItemClear() {
            if(mode != Mode.SELECTION_MODE) {
                wasSelected = false;
                superNotes.clear();
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
                        Intent intent = new Intent(mContext, SimpleNoteActivity.class);
                        intent.putExtra(Constants.TASK, notes.get(position));
                        intent.putExtra(Constants.KIND, mActivity.currentKind);
                        mContext.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    case 1:
                        Intent intent1 = new Intent(mContext, ListNoteActivity.class);
                        intent1.putExtra(Constants.LIST_TASK, notes.get(position));
                        intent1.putExtra(Constants.KIND, mActivity.currentKind);
                        mContext.startActivity(intent1);
                        mActivity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                }
            }
            else if (mode == Mode.SELECTION_MODE) {
                if (selects[position]) {
                    superNotes.remove(notes.get(position));
                    selects[position] = false;
                    selectedNotesCounter--;
                    mActivity.selectedItemCount(selectedNotesCounter);
                    if(selectedNotesCounter ==0) {
                        setSelectionMode(Mode.NORMAL);
                    }
                } else {
                    if(!superNotes.contains(notes.get(position)))
                    superNotes.add(notes.get(position));
                    selectedNotesCounter++;
                    mActivity.selectedItemCount(selectedNotesCounter);
                    selects[position] = true;
                }

            }
            notifyItemChanged(getAdapterPosition());
        }

        @Override
        public void onItemSelected2() {
            System.out.println("        ItemSELECTED 2");
            if(mode == Mode.NORMAL)
                mActivity.setSelectionMode();
            onClick(new View(mContext));
        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mActivity.hideFabs();
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

    public List<SuperNote> getSuperNotes() {
        return superNotes;
    }

    private void setColorCardView(CardView cardView, int position){
        if(mode == Mode.SELECTION_MODE) {
            if (superNotes.contains(notes.get(position))) {
                cardView.setCardBackgroundColor(Color.LTGRAY);
            } else {
                if (position >= notes.size())
                    return;
                switch (notes.get(position).getColor()) {
                    case Constants.GREEN:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightGreen));
                        break;
                    case Constants.RED:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightRed));
                        break;
                    case Constants.YELLOW:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightYellow));
                        break;
                    case Constants.BLUE:
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightLightBlue));
                        break;
                    case 0:
                        cardView.setCardBackgroundColor(Color.WHITE);
                        break;
                }
            }
            return;
        }

        if (position >= notes.size())
            return;
        switch (notes.get(position).getColor()) {
            case Constants.GREEN:
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightGreen));
                break;
            case Constants.RED:
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightRed));
                break;
            case Constants.YELLOW:
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightYellow));
                break;
            case Constants.BLUE:
                cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.taskColorLightLightBlue));
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
                SimpleNote simpleNote = (SimpleNote) notes.get(position);
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

                if(notes.get(position).isRemind()){
                    holder.alarm.setVisibility(View.VISIBLE);
                } else
                    holder.alarm.setVisibility(View.GONE);

                break;

            case 1:
                listNote = (ListNote) notes.get(position);
                holder.listHeadEditText.setTextSize(textSize);
                if(listNote.getHeadLine()!=null && listNote.getHeadLine().length()==0) {
                    holder.listHeadEditText.setVisibility(View.GONE);
                }
                else {
                    holder.listHeadEditText.setVisibility(View.VISIBLE);
                    holder.listHeadEditText.setText(listNote.getHeadLine());
                }

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService
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
                if(notes.get(position).isRemind()){
                    holder.alarm.setVisibility(View.VISIBLE);
                } else
                    holder.alarm.setVisibility(View.GONE);

                break;
        }

        if(mActivity.ismNotification_on()){
            holder.notifInfoContainer.setVisibility(View.VISIBLE);
            if(holder.notifInfo!=null) {

                SuperNote task = notes.get(position);

                String repeating;

                java.text.DateFormat dateFormat = DateFormat.getDateFormat(mContext);
                java.text.DateFormat timeFormat = DateFormat.getTimeFormat(mContext);

                String formattedDate = dateFormat.format(task.getReminderTime()) + " " + timeFormat.format(task.getReminderTime());

                if(task.getRepeatingPeriod() == Constants.PERIOD_ONE_DAY){
                    repeating = mActivity.getResources().getString(R.string.every_day);
                } else if(task.getRepeatingPeriod() == Constants.PERIOD_WEEK){
                    repeating = mActivity.getResources().getString(R.string.every_week);
                } else if(task.getRepeatingPeriod() == Constants.PERIOD_MONTH){
                    repeating = mActivity.getResources().getString(R.string.every_month);
                } else {
                    repeating = "";
                }

                holder.notifInfo.setText(formattedDate + " " + repeating);
            }
        } else {
            holder.notifInfoContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (notes.get(position) instanceof SimpleNote)
            return 0;
        if(notes.get(position) instanceof ListNote) {
            return 1;
        }
        return -1;
    }

    public void dataChanged(List<SuperNote> tasks){
        this.notes = tasks;
        if(!mActivity.is_in_action_mode()){
            selects = new boolean[tasks.size()];
        }
        textSize = mSharedPreferences.getInt("cardFontSize", 16);
        compareTasks();
        selectedNotesCopy = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void compareTasks(){
        Comparator<SuperNote> comparator = new Comparator<SuperNote>() {
            @Override
            public int compare(SuperNote superTask, SuperNote t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(notes, comparator);
    }
    private void compareSelectionTasks(){
        Comparator<SuperNote> comparator = new Comparator<SuperNote>() {
            @Override
            public int compare(SuperNote superTask, SuperNote t1) {
                return superTask.getPosition() < t1.getPosition() ? 1 : -1;
            }
        };
        Collections.sort(notes, comparator);
    }

    private void setRightPosition(){
        for (int i = 0; i < notes.size(); i++) {
            notes.get(i).setPosition(notes.size()-i-1);
        }
    }
}
