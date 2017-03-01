package com.example.dmitryvedmed.taskbook;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ListTaskRecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>  {




    @Override
    public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent,false);
       // RecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new RecyclerAdapter.RecyclerViewHolder(view);

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
