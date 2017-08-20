package com.example.dmitryvedmed.taskbook.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperAdapter;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperViewHolder;
import com.example.dmitryvedmed.taskbook.logic.Section;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SectionPositionRecyclerAdapter extends RecyclerView.Adapter<SectionPositionRecyclerAdapter.RecyclerViewHolder> implements ItemTouchHelperAdapter {

    private List<Section> sections;

    public List<Section> getSections() {
        return sections;
    }

    public SectionPositionRecyclerAdapter(List<Section> sections) {
        this.sections = sections;
        compareSections();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Section prev = sections.remove(fromPosition);
        sections.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        setRightPosition();
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemSelected() {

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        private TextView textView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.section_name);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemSelected2() {
        }

        @Override
        public void onItemClear() {
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, parent,false);
        RecyclerViewHolder  recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.textView.setText(sections.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    private void compareSections(){
        Comparator<Section> comparator = new Comparator<Section>() {
            @Override
            public int compare(Section section, Section t1) {
                return section.getPosition() < t1.getPosition() ? -1 : 1;
            }
        };
        Collections.sort(sections, comparator);
    }

    private void setRightPosition(){
        Log.d("TAG", "       Adapter --- setRightPosition");
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setPosition(i);
        }
    }
}