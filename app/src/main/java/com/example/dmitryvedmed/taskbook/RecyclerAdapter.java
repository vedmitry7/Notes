package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private String[] image_urls;
    private Context context;

    public RecyclerAdapter(String[] names, Context context) {
        this.image_urls = names;
        this.context = context;
        System.out.println("rv constructor" + " " + image_urls.length);
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
        holder.headTextView.setText(image_urls[position]);
        holder.taskTextView.setText(image_urls[position]+image_urls[position]+image_urls[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(image_urls[position]);
            }
        });
        System.out.println(position);
    }


    @Override
    public int getItemCount() {
        return image_urls.length;
    }
}