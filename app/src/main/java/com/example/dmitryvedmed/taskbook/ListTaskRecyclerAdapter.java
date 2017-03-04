package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


public class ListTaskRecyclerAdapter extends RecyclerView.Adapter<ListTaskRecyclerAdapter.RecyclerViewHolder>  {

    private Context context;
    private ListTask listTask;
    private boolean onBind;


    public ListTaskRecyclerAdapter(ListTask listTask, Context context) {
        this.context = context;
        this.listTask = listTask;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private EditText editText;
        private EditTextListener editTextListener;
        private Button button;
        private CheckBoxListener checkBoxListener;
        private CheckBox checkBox;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ///????
            System.out.println("TYPE = " + this.getItemViewType());
            editText = (EditText) itemView.findViewById(R.id.itemListEditText);
            editTextListener = new EditTextListener();
            if(editText != null) {
                editText.addTextChangedListener(editTextListener);
                //editText.setOnFocusChangeListener(editTextListener);
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                            listTask.getUncheckedTasks().add("");
                            update();
                            return true;
                        }
                        return false;
                    }
                });

            }


            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBoxListener = new CheckBoxListener();
            if(checkBox != null)
                checkBox.setOnCheckedChangeListener(checkBoxListener);

            button = (Button) itemView.findViewById(R.id.delButton);

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
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listTask.getUncheckedTasks().add("");
                        update();
                        System.out.println("                    CLiCK");
                    }
                });
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {


        System.out.println(position + "-" +  holder.getItemViewType());

        if(position < listTask.getUncheckedTasks().size()) {
            holder.editTextListener.updatePosition(holder.getAdapterPosition());
            holder.editText.setText(listTask.getUncheckedTasks().get(position));
            holder.checkBoxListener.updatePosition(position);

            onBind = true;
            holder.checkBox.setChecked(false);
            onBind = false;
            System.out.println(position + " - " + listTask.getUncheckedTasks().get(position));

            holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b)
                        holder.button.setVisibility(View.VISIBLE);
                    else
                        holder.button.setVisibility(View.INVISIBLE);
                }
            });
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(position + " BUTTON          CLICK");
                    listTask.getUncheckedTasks().remove(position);
                    update();
                }
            });
        }
        if(position > listTask.getUncheckedTasks().size())
        {
            System.out.println("POSITION = " + position);
            holder.editTextListener.updatePosition(holder.getAdapterPosition());
            String s = ("<u>" + listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size()+1)) + "</u>");
            Spanned spanned = android.text.Html.fromHtml(s);

            System.out.println("  -----------" + spanned);
            holder.editText.setText(spanned);
            holder.editText.setAlpha(0.5f);
            holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b)
                        holder.button.setVisibility(View.VISIBLE);
                    else
                        holder.button.setVisibility(View.INVISIBLE);
                }
            });
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(position + " BUTTON          CLICK");
                    listTask.getCheckedTasks().remove(position - (listTask.getUncheckedTasks().size()+1));
                    update();

                }
            });
            holder.checkBoxListener.updatePosition(position);
            onBind = true;
            holder.checkBox.setChecked(true);
            onBind = false;
            System.out.println(position + " - " + listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size()+1)));
        }
    }

    private void update(){
        notifyDataSetChanged();
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

    private class EditTextListener implements TextWatcher, View.OnFocusChangeListener {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(position<listTask.getUncheckedTasks().size())
                listTask.getUncheckedTasks().set(position, charSequence.toString());
            if(position > listTask.getUncheckedTasks().size())
                listTask.getCheckedTasks().set(position - (listTask.getUncheckedTasks().size() + 1), charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }


        @Override
        public void onFocusChange(View view, boolean b) {
            if(b)
                System.out.println("EDIT TEXT _  " + position + "Has focus");
        }
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
                    update();
                } else {
                    listTask.getUncheckedTasks().add(listTask.getCheckedTask(position - (listTask.getUncheckedTasks().size() + 1)));
                    listTask.getCheckedTasks().remove(position - (listTask.getUncheckedTasks().size()));
                    update();
                }
            }
        }
    }


}
