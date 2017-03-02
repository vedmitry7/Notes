package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;


public class ListTaskRecyclerAdapter extends RecyclerView.Adapter<ListTaskRecyclerAdapter.RecyclerViewHolder>  {

    private Context context;
    private ListTask listTask;


    public ListTaskRecyclerAdapter(ListTask listTask, Context context) {
        this.context = context;
        this.listTask = listTask;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private EditText editText;
        private CheckBox checkBox;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.itemListEditText);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            System.out.println("ViewHolder constructor");
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_task_activity, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_list_task_button, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        System.out.println(position + "-" +  holder.getItemViewType());

        if(position < listTask.getUncheckedTasks().size()) {
            holder.editText.setText(listTask.getUncheckedTasks().get(position));
            System.out.println(position + " - " + listTask.getUncheckedTasks().get(position));
        }
        if(position>3)
        {
            System.out.println("POSITION = " + position);
            holder.editText.setText(listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size()+1)));
            holder.checkBox.setChecked(true);
            System.out.println(position + " - " + listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size()+1)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listTask.getUncheckedTasks().size())
            return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return listTask.getUncheckedTasks().size() + listTask.getCheckedTasks().size()+1;
    }
}
