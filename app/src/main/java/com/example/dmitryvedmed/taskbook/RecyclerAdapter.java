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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<Task> tasks;
    private Context context;

    public RecyclerAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        System.out.println("rv constructor" + " " + tasks.size());
    }

    public void dataChanged( List<Task> tasks){

        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView headTextView, taskTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            System.out.println("rvh constructor with view");
            headTextView = (TextView) itemView.findViewById(R.id.headTextView);
            taskTextView = (TextView) itemView.findViewById(R.id.taskTextView);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.headTextView.setText(tasks.get(position).getHeadLine());
        holder.taskTextView.setText(tasks.get(position).getContext());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
               // intent.putExtra("id", tasks.get(position).getId());
                intent.putExtra("Task", (Serializable) tasks.get(position));
                context.startActivity(intent);
            }
        });
        System.out.println(position);
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public void refreshList(List<Task> tasks){
        this.tasks = tasks;
    }
}