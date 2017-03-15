package com.example.dmitryvedmed.taskbook;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperAdapter;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperViewHolder;

import java.io.Serializable;
import java.util.List;

import static com.example.dmitryvedmed.taskbook.R.id.headTextView;
import static com.example.dmitryvedmed.taskbook.R.id.taskTextView;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {

    private List<SuperTask> tasks;
    private Context context;
    private SimpleTask simpleTask;
    private ListTask listTask;
    private TextView textView;
    Typeface typeFace ;
    Typeface boldTypeFace ;

    public MainRecyclerAdapter() {
    }

    public MainRecyclerAdapter(List<SuperTask> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
        System.out.println("rv constructor" + " " + tasks.size());
        textView = new TextView(context);
        textView.setText("1234we5r");
         typeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
         boldTypeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
    }

    @Override
    public void onItemDismiss(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        SuperTask prev = tasks.remove(fromPosition);
        tasks.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        private TextView stHeadLine, stContent, listHeadEditText, ltFirst, ltSecond;
        private LinearLayout layout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
                    stHeadLine = (TextView) itemView.findViewById(headTextView);
                    stContent = (TextView) itemView.findViewById(taskTextView);

                   if(stContent!=null){
                    stHeadLine.setTypeface(boldTypeFace);
                    stContent.setTypeface(typeFace);}

                    listHeadEditText = (TextView) itemView.findViewById(R.id.mainRecListItemHead);
                    //ltFirst = (TextView) itemView.findViewById(R.id.textView4);
                    //ltSecond = (TextView) itemView.findViewById(R.id.textView3);
                    layout = (LinearLayout) itemView.findViewById(R.id.card_view_list_layout);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
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
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        switch (getItemViewType(position)){

            case 0:
                simpleTask = (SimpleTask) tasks.get(position);
                if(simpleTask.getHeadLine().equals(""))
                    holder.stHeadLine.setVisibility(View.GONE);
                else
                holder.stHeadLine.setText(simpleTask.getHeadLine());
                if(simpleTask.getContext().equals(""))
                    holder.stContent.setVisibility(View.GONE);
                holder.stContent.setText(simpleTask.getContext());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TaskActivity.class);
                        // intent.putExtra("id", tasks.get(position).getId());
                        intent.putExtra("Task", (Serializable) tasks.get(position));
                        context.startActivity(intent);
                    }
                });
                break;
            case 1:
               listTask = (ListTask) tasks.get(position);
             /*        holder.ltFirst.setText(listTask.getUncheckedTask(0));
                    holder.ltSecond.setText(listTask.getUncheckedTask(1));*/
                if(listTask.getHeadLine().equals("")) {
                    System.out.println(" EQUALS listTask.getHeadLine() - " + listTask.getHeadLine());
                    holder.listHeadEditText.setVisibility(View.GONE);
                }
                else {
                    System.out.println(" NOT EQUALS listTask.getHeadLine() - " + listTask.getHeadLine());
                    holder.listHeadEditText.setText(listTask.getHeadLine());
                }


                LayoutInflater inflater = (LayoutInflater)context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);

                holder.layout.removeAllViews();
                for (String s:listTask.getUncheckedTasks()
                     ) {
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBox);
                   // c.setPressed(true);
                    t.setTypeface(typeFace);
                    t.setText(s);
                    holder.layout.addView(view);
                }
                for (String s:listTask.getCheckedTasks()
                        ) {
                    View view = inflater.inflate(R.layout.card_view_list_item, null, false);
                    TextView t = (TextView) view.findViewById(R.id.textView3);
                    ImageButton c = (ImageButton) view.findViewById(R.id.checkBox);
                    c.setPressed(true);
                    t.setTypeface(typeFace);
                    t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    t.setText(s);
                    holder.layout.addView(view);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ListTaskActivity.class);
                        intent.putExtra("ListTask", tasks.get(position));
                        context.startActivity(intent);
                    }
                });
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

    public void dataChanged(List<SuperTask> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }
}
