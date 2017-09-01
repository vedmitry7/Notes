package com.vedmitryapps.notes.ui;


import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vedmitryapps.notes.R;
import com.vedmitryapps.notes.logic.DBHelper;
import com.vedmitryapps.notes.logic.ListNote;
import com.vedmitryapps.notes.untils.SingletonFonts;


public class ListNoteDialogRecyclerAdapter extends RecyclerView.Adapter<ListNoteDialogRecyclerAdapter.RecyclerViewHolder> {

    private ListNoteDialogActivity mActivity;
    private boolean onBind;
    private ListNote listNote;
    private DBHelper mDbHelper;
    private View mView;


    public ListNoteDialogRecyclerAdapter(ListNote task, ListNoteDialogActivity listNoteDialogActivity) {
        mActivity = listNoteDialogActivity;
        this.listNote = task;
        mDbHelper = new DBHelper(mActivity);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CheckBox checkBox;
        private CheckBoxListener checkBoxListener;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            if(itemView.isSelected())
            mView = itemView.findViewById(R.id.deliver);

            if(mView !=null && (listNote.getCheckedItems().size()==0|| listNote.getUncheckedItems().size()==0))
            mView.setVisibility(View.GONE);
            checkBoxListener = new CheckBoxListener();

            switch (getItemViewType()){
                case 0:
                    break;
                case 1:
                    break;
            }

            textView = (TextView) itemView.findViewById(R.id.text_item_list_task_dialog);
            if(textView!=null) {
                textView.setTypeface(SingletonFonts.getInstance(mActivity).getRobotoRegular());
                textView.setHint("");
            }


            checkBox = (CheckBox) itemView.findViewById(R.id.checkBoxDialog);
            if(checkBox != null) {
                checkBox.setOnCheckedChangeListener(checkBoxListener);
            }
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListNoteDialogRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_note_dialog_activity, parent,false);
                recyclerViewHolder = new ListNoteDialogRecyclerAdapter.RecyclerViewHolder(view);
                break;
            case 1:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.deliver, parent,false);
                view1.setSelected(true);
                recyclerViewHolder = new ListNoteDialogRecyclerAdapter.RecyclerViewHolder(view1);
                break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        switch (holder.getItemViewType()){
            case 0:
                break;
            case 1:
                break;
        }
        if(position < listNote.getUncheckedItems().size()) {
            holder.textView.setText(listNote.getUncheckedItems().get(position));
            holder.checkBoxListener.updatePosition(position);

            onBind = true;
            holder.checkBox.setChecked(false);
            holder.checkBox.setSelected(false);
            onBind = false;

            holder.textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            holder.textView.setAlpha(1f);

        }

        if(position > listNote.getUncheckedItems().size()) {
            String s = (listNote.getCheckedItems().get(position - (listNote.getUncheckedItems().size() + 1)));
            holder.textView.setText(s);
            holder.textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textView.setAlpha(0.5f);

            holder.checkBoxListener.updatePosition(position);
            onBind = true;
            holder.checkBox.setChecked(true);
            holder.checkBox.setSelected(true);
            onBind = false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == listNote.getUncheckedItems().size())
            return 1;
        else
            return 0;
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
                    listNote.getCheckedItems().add(listNote.getUncheckedItems(position));
                    listNote.getUncheckedItems().remove(position);
                    if(mView !=null)
                        mView.setVisibility(View.VISIBLE);
                    if(mView !=null&& listNote.getUncheckedItems().size()==0)
                        mView.setVisibility(View.GONE);
                    update();
                } else {
                    if(mView !=null&& listNote.getUncheckedItems().size()==0)
                        mView.setVisibility(View.VISIBLE);
                    listNote.getUncheckedItems().add(listNote.getCheckedItem(position - (listNote.getUncheckedItems().size() + 1)));
                    listNote.getCheckedItems().remove(position - (listNote.getUncheckedItems().size()));
                    if(mView !=null && listNote.getCheckedItems().size()==0)
                        mView.setVisibility(View.GONE);
                    update();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listNote.getUncheckedItems().size() + listNote.getCheckedItems().size()+1;
    }

    private void update(){
        mDbHelper.updateNote(listNote, null);
        notifyDataSetChanged();
    }
}
