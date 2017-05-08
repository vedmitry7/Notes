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
import com.example.dmitryvedmed.taskbook.logic.DBHelper5;
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

public class ListTaskDialogRecyclerAdapter extends RecyclerView.Adapter<ListTaskDialogRecyclerAdapter.RecyclerViewHolder> {

    private ListTaskDialogActivity activity;
    private boolean onBind;
    private ListTask listTask;
    private DBHelper5 dbHelper;
    private View view;


    public ListTaskDialogRecyclerAdapter(ListTask task, ListTaskDialogActivity listTaskDialogActivity) {
        activity = listTaskDialogActivity;
        this.listTask = task;
        dbHelper = new DBHelper5(activity);
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

            if(view!=null && (listTask.getCheckedTasks().size()==0||listTask.getUncheckedTasks().size()==0))
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
        ListTaskDialogRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_task_dialog_activity, parent,false);
                recyclerViewHolder = new ListTaskDialogRecyclerAdapter.RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.deliver, parent,false);
                view1.setSelected(true);
                recyclerViewHolder = new ListTaskDialogRecyclerAdapter.RecyclerViewHolder(view1);
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
        if(position < listTask.getUncheckedTasks().size()) {
            holder.textView.setText(listTask.getUncheckedTasks().get(position));
            holder.checkBoxListener.updatePosition(position);

            onBind = true;
            holder.checkBox.setChecked(false);
            onBind = false;

            holder.textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            holder.textView.setAlpha(1f);

        }

        if(position > listTask.getUncheckedTasks().size()) {
            String s = (listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size() + 1)));
            holder.textView.setText(s);
            holder.textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textView.setAlpha(0.5f);

            holder.checkBoxListener.updatePosition(position);
            onBind = true;
            holder.checkBox.setChecked(true);
            onBind = false;
            System.out.println(position + " - " + listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size() + 1)));


        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listTask.getUncheckedTasks().size())
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
                    listTask.getCheckedTasks().add(listTask.getUncheckedTask(position));
                    listTask.getUncheckedTasks().remove(position);
                    if(view!=null)
                        view.setVisibility(View.VISIBLE);
                    if(view!=null&&listTask.getUncheckedTasks().size()==0)
                        view.setVisibility(View.GONE);
                    update();
                } else {
                    if(view!=null&&listTask.getUncheckedTasks().size()==0)
                        view.setVisibility(View.VISIBLE);
                    listTask.getUncheckedTasks().add(listTask.getCheckedTask(position - (listTask.getUncheckedTasks().size() + 1)));
                    listTask.getCheckedTasks().remove(position - (listTask.getUncheckedTasks().size()));
                    Log.d("TAG", "view null  " + (view == null));
                    Log.d("TAG", "list null  " + (listTask.getCheckedTasks().size()==0));
                    if(view!=null && listTask.getCheckedTasks().size()==0)
                        view.setVisibility(View.GONE);
                    update();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listTask.getUncheckedTasks().size() + listTask.getCheckedTasks().size()+1;
    }

    private void update(){
        Log.d("TAG", "update " );
        dbHelper.updateTask(listTask, null);
        notifyDataSetChanged();
    }
}
