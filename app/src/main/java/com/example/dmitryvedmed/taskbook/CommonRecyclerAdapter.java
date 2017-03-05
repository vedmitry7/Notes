package com.example.dmitryvedmed.taskbook;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.id.headTextView;
import static com.example.dmitryvedmed.taskbook.R.id.taskTextView;

public class CommonRecyclerAdapter extends RecyclerView.Adapter<CommonRecyclerAdapter.RecyclerViewHolder>{

    private List<SuperTask> tasks;
    private Context context;

    public CommonRecyclerAdapter(List<SuperTask> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        System.out.println("rv constructor" + " " + tasks.size());
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private TextView stHeadLine, stContent, ltFirst, ltSecond;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            stHeadLine = (TextView) itemView.findViewById(headTextView);
            stContent = (TextView) itemView.findViewById(taskTextView);

            ltFirst = (TextView) itemView.findViewById(R.id.textView4);
            ltSecond = (TextView) itemView.findViewById(R.id.textView3);
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType) {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_activity_recycler_item, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
switch (getItemViewType(position)){

    case 0:
        break;
    case 1:
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
}
