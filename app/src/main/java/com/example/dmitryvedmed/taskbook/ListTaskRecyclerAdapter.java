package com.example.dmitryvedmed.taskbook;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ListTaskRecyclerAdapter extends RecyclerView.Adapter<ListTaskRecyclerAdapter.RecyclerViewHolder>  {

    private Context context;
    private ListTask listTask;
    private boolean onBind;
    private List<EditText> editTexts;
    private int hasInsertInside = -1;


    public ListTaskRecyclerAdapter(ListTask listTask, Context context) {
        this.context = context;
        this.listTask = listTask;
        if(listTask.getId() == -1)
            listTask.getUncheckedTasks().add("");
        editTexts = new ArrayList<>();
    }

    public ListTask getListTask() {
        return listTask;
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

            Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");

            editTextListener = new EditTextListener();
            if(editText != null) {
                editText.setTypeface(typeFace);
                editText.addTextChangedListener(editTextListener);
               /* editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                            listTask.getUncheckedTasks().add("");
                            //update();
                            notifyItemChanged(listTask.getUncheckedTasks().size()-1);
                            return true;
                        }
                        return false;
                    }
                });*/
                //editText.requestFocus();
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
                        //update();
                        notifyItemChanged(listTask.getUncheckedTasks().size());
                        notifyItemChanged(listTask.getUncheckedTasks().size()-1);
                        System.out.println("                    CLiCK");
                    }
                });
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        //Log.d("TAG", "ON BIND VIEW HOLDER" );

        Log.d("TAG", "POSITION -" + position + "," +" TYPE -"+  holder.getItemViewType() );

        if(position < listTask.getUncheckedTasks().size()) {
            holder.editTextListener.updatePosition(holder.getAdapterPosition());
            holder.editText.setText(listTask.getUncheckedTasks().get(position));
            holder.checkBoxListener.updatePosition(position);

            onBind = true;
            holder.checkBox.setChecked(false);
            onBind = false;

            holder.editText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            holder.editText.setAlpha(1f);

            holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.d("TAG", "ET " + position + " FOCUS " + b );
                    if(b) {
                        holder.button.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.button.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Log.d("TAG",  "Pos - " + position );

            if(hasInsertInside!=-1&&position==hasInsertInside){
                holder.editText.requestFocus();
                holder.button.setVisibility(View.VISIBLE);
                hasInsertInside=-1;
            }
            if(position==listTask.getUncheckedTasks().size()-1 && hasInsertInside==-1) {
                Log.d("TAG", "ET " + position + " LIST " + (listTask.getUncheckedTasks().size()-1) );
                holder.editText.requestFocus();
                holder.button.setVisibility(View.VISIBLE);
            } else {
                holder.button.setVisibility(View.INVISIBLE);

            }

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listTask.getUncheckedTasks().remove(position);
                    update();
                    //notifyItemChanged(position);
                    //notifyItemRangeChanged(position,2);
                }
            });
        }
        if(holder.editText!=null)
        holder.editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    listTask.getUncheckedTasks().add(position + 1, "");
                    update();
                    if(position<listTask.getUncheckedTasks().size()-1)
                        hasInsertInside = position;

                    //notifyItemChanged(position+1);
                    return true;
                }
                return false;
            }
        });

        if(position > listTask.getUncheckedTasks().size())
        {
            holder.editTextListener.updatePosition(holder.getAdapterPosition());
            String s = (listTask.getCheckedTasks().get(position - (listTask.getUncheckedTasks().size()+1)));
            holder.editText.setText(s);
            holder.editText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
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
                    //update();
                    notifyItemChanged(position);

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
