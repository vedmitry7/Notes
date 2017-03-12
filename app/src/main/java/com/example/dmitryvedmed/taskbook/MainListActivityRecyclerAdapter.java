package com.example.dmitryvedmed.taskbook;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class MainListActivityRecyclerAdapter extends RecyclerView.Adapter<MainListActivityRecyclerAdapter.RecyclerViewHolder>  {

    private List<ListTask> tasks;
    private Context context;

    public MainListActivityRecyclerAdapter(List<ListTask> tasks, Context context) {
        this.context = context;
        this.tasks = tasks;
        System.out.println("LIST SIZE = " + tasks.size());
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView first, second;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            System.out.println("rvh constructor with view");
         //   first = (TextView) itemView.findViewById(R.id.textView4);
         //  second = (TextView) itemView.findViewById(R.id.textView3);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_activity_recycler_item, parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        try {
            holder.first.setText(tasks.get(position).getUncheckedTask(0));
        }
        catch (IndexOutOfBoundsException e){
            holder.first.setText("null");
        }

        try {
            holder.second.setText(tasks.get(position).getUncheckedTask(1));
        }
        catch (IndexOutOfBoundsException e){
            holder.second.setText("null");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ListTaskActivity.class);
                intent.putExtra("ListTask", (Serializable) tasks.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void dataChanged( List<ListTask> tasks){

        this.tasks = tasks;
        notifyDataSetChanged();
    }
}
