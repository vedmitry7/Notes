package com.example.dmitryvedmed.taskbook.ui;


import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.logic.DBHelper;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

public class ListNoteDialogRecyclerAdapter extends RecyclerView.Adapter<ListNoteDialogRecyclerAdapter.RecyclerViewHolder> {

    private ListNoteDialogActivity activity;
    private boolean onBind;
    private ListNote listNote;
    private DBHelper dbHelper;
    private View view;


    public ListNoteDialogRecyclerAdapter(ListNote task, ListNoteDialogActivity listNoteDialogActivity) {
        activity = listNoteDialogActivity;
        this.listNote = task;
        dbHelper = new DBHelper(activity);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CheckBox checkBox;
        private CheckBoxListener checkBoxListener;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            System.out.println("ViewHolder constructor ");
            System.out.println("TYPE = " + this.getItemViewType());


            if(itemView.isSelected())
            view = itemView.findViewById(R.id.deliver);

            if(view!=null && (listNote.getCheckedTasks().size()==0|| listNote.getUncheckedTasks().size()==0))
            view.setVisibility(View.GONE);
            checkBoxListener = new CheckBoxListener();

            switch (getItemViewType()){
                case 0:
                    break;
                case 1:
                    break;
            }

            textView = (TextView) itemView.findViewById(R.id.text_item_list_task_dialog);
            if(textView!=null) {
                textView.setTypeface(SingletonFonts.getInstance(activity).getRobotoRegular());
            }


            checkBox = (CheckBox) itemView.findViewById(R.id.checkBoxDialog);
            if(checkBox != null) {
                checkBox.setOnCheckedChangeListener(checkBoxListener);
            }
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListNoteDialogRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_note_dialog_activity, parent,false);
                recyclerViewHolder = new ListNoteDialogRecyclerAdapter.RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.deliver, parent,false);
                view1.setSelected(true);
                recyclerViewHolder = new ListNoteDialogRecyclerAdapter.RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        String type = holder.getItemViewType() == 0 ? "editText":"button";
        Log.d("TAG", "POSITION -" + position + "," +" TYPE -" +  type );


        switch (holder.getItemViewType()){
            case 0:
                break;
            case 1:
                break;
        }
        if(position < listNote.getUncheckedTasks().size()) {
            holder.textView.setText(listNote.getUncheckedTasks().get(position));
            holder.checkBoxListener.updatePosition(position);

            onBind = true;
            holder.checkBox.setChecked(false);
            onBind = false;

            holder.textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            holder.textView.setAlpha(1f);

        }

        if(position > listNote.getUncheckedTasks().size()) {
            String s = (listNote.getCheckedTasks().get(position - (listNote.getUncheckedTasks().size() + 1)));
            holder.textView.setText(s);
            holder.textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textView.setAlpha(0.5f);

            holder.checkBoxListener.updatePosition(position);
            onBind = true;
            holder.checkBox.setChecked(true);
            onBind = false;
            System.out.println(position + " - " + listNote.getCheckedTasks().get(position - (listNote.getUncheckedTasks().size() + 1)));


        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listNote.getUncheckedTasks().size())
            return 1;
        else
            return 0;
    }

    private class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!onBind) {
                if (b) {
                    listNote.getCheckedTasks().add(listNote.getUncheckedTask(position));
                    listNote.getUncheckedTasks().remove(position);
                    if(view!=null)
                        view.setVisibility(View.VISIBLE);
                    if(view!=null&& listNote.getUncheckedTasks().size()==0)
                        view.setVisibility(View.GONE);
                    update();
                } else {
                    if(view!=null&& listNote.getUncheckedTasks().size()==0)
                        view.setVisibility(View.VISIBLE);
                    listNote.getUncheckedTasks().add(listNote.getCheckedTask(position - (listNote.getUncheckedTasks().size() + 1)));
                    listNote.getCheckedTasks().remove(position - (listNote.getUncheckedTasks().size()));
                    Log.d("TAG", "view null  " + (view == null));
                    Log.d("TAG", "list null  " + (listNote.getCheckedTasks().size()==0));
                    if(view!=null && listNote.getCheckedTasks().size()==0)
                        view.setVisibility(View.GONE);
                    update();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listNote.getUncheckedTasks().size() + listNote.getCheckedTasks().size()+1;
    }

    private void update(){
        Log.d("TAG", "update " );
        dbHelper.updateTask(listNote, null);
        notifyDataSetChanged();
    }
}
