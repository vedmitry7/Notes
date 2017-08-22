package com.example.dmitryvedmed.taskbook.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmitryvedmed.taskbook.R;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperAdapter;
import com.example.dmitryvedmed.taskbook.helper.ItemTouchHelperViewHolder;
import com.example.dmitryvedmed.taskbook.logic.ListNote;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ListNoteRecyclerAdapter extends RecyclerView.Adapter<ListNoteRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private ListNote listNote;
    private boolean onBind;
    private List<EditText> editTexts;
    private ListNoteActivity mActivity;
    private int fromPos, toPos;
    private SharedPreferences mSharedPreferences;

    public ListNoteRecyclerAdapter(ListNote listNote, Context mContext) {
        this.mContext = mContext;
        mActivity = (ListNoteActivity) mContext;
        this.listNote = listNote;
        if(listNote.getId() == -1) {
            listNote.setHeadLine("");
            listNote.getUncheckedItems().add("");
        }
        editTexts = new ArrayList<>();

        mSharedPreferences = mActivity.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
    }

    public ListNote getListNote() {
        return listNote;
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        mActivity.setItemMovement(false);

        int realFromPos = fromPosition-1;
        int realToPos = toPosition -1;

        if(toPosition==0)
            return;

        if(fromPosition> listNote.getUncheckedItems().size()+1){
            int fromP = realFromPos - (listNote.getUncheckedItems().size()+1);
            int toP = realToPos - (listNote.getUncheckedItems().size()+1);
            if(toP<0) {
                return;
            }
            String prev = listNote.getCheckedItems().remove(fromP);
            listNote.getCheckedItems().add(toP, prev);
            notifyItemMoved(fromPosition, toPosition);
            notifyItemChanged(fromPosition);
            notifyItemChanged(toPosition);
            return;
        }

        if(toPosition>= listNote.getUncheckedItems().size()+1)
            return;

        String prev = listNote.getUncheckedItems().remove(realFromPos);
        listNote.getUncheckedItems().add(realToPos, prev);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onItemSelected() {
    }

    void deleteCheckedTasks() {
        listNote.getCheckedItems().clear();
        update();
        requestFocusLast();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnTouchListener {
        private EditText editText;
        private EditText headLineEditText;

        private EditTextListener editTextListener;
        private CheckBoxListener checkBoxListener;
        private Button button;
        private ImageView imageView;
        private CheckBox checkBox;
        private TextView newPoint;
        private View deliver;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            editTextListener = new EditTextListener();
            checkBoxListener = new CheckBoxListener();

            deliver =  itemView.findViewById(R.id.deliver_rec);

            switch (getItemViewType()){
                case 0:
                    break;
                case 1:
                    break;
            }

            editText = (EditText) itemView.findViewById(R.id.itemListEditText);
            if(editText!=null) {
               // editText.setTypeface(SingletonFonts.getInstance(mContext).getRobotoRegular());
                editText.setTextSize(mSharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16));
                editText.addTextChangedListener(editTextListener);
            }

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            if(checkBox != null) {
                checkBox.setOnCheckedChangeListener(checkBoxListener);
            }

            button = (Button) itemView.findViewById(R.id.delButton);
            imageView = (ImageView) itemView.findViewById(R.id.drag);
            if(imageView!=null) {
                imageView.setOnTouchListener(this);
            }
            newPoint = (TextView)itemView.findViewById(R.id.newPoint);
            if(newPoint!=null) {
                newPoint.setTypeface(SingletonFonts.getInstance(mContext).getRobotoRegular());
                newPoint.setTextSize(mSharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16));
            }

            headLineEditText = (EditText) itemView.findViewById(R.id.listHeadEditText2);
            if(headLineEditText!=null) {
                headLineEditText.setTextSize(mSharedPreferences.getInt(Constants.TASK_FONT_SIZE, 16));
                headLineEditText.clearFocus();
                headLineEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            setFocusToEditText();
                            headLineEditText.clearFocus();
                            return true;
                        }
                        return false;
                    }
                });
                headLineEditText.addTextChangedListener(new HeadLineEditTextListener());
            }
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemSelected2() {
        }

        @Override
        public void onItemClear() {
            notifyItemChanged(fromPos);
            notifyItemChanged(toPos);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mActivity.setItemMovement(true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return false;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolder recyclerViewHolder = null;
        switch (viewType)
        {
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_note_activity, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view);
                break;
            case 1:
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_list_task_button, parent,false);
            recyclerViewHolder = new RecyclerViewHolder(view1);
            view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listNote.getUncheckedItems().add("");
                        update();
                        requestFocusLast();
                    }
                });
            break;
            case 2:
               View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_line_list_note_recycler_item, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view2);
            break;
        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        if(holder.headLineEditText!=null) {
            holder.headLineEditText.setText(listNote.getHeadLine());
            holder.headLineEditText.setSelection(holder.headLineEditText.getText().length());
        }

        switch (holder.getItemViewType()){
            case 0:
                final int realPosition = position - 1;
                if(realPosition < listNote.getUncheckedItems().size()) {
                    holder.editTextListener.updatePosition(realPosition);
                    holder.editText.setText(listNote.getUncheckedItems().get(realPosition));
                    holder.checkBoxListener.updatePosition(realPosition);

                    onBind = true;
                    holder.checkBox.setChecked(false);
                    onBind = false;

                    holder.editText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    holder.editText.setAlpha(0.87f);

                    holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if(b) {
                                holder.button.setVisibility(VISIBLE);
                                holder.editText.setSelection(holder.editText.getText().length());
                            }
                            else {
                                holder.button.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listNote.getUncheckedItems().remove(realPosition);
                            editTexts.remove(realPosition);
                            update();
                            requestFocusTo(realPosition-1);
                        }
                    });

                    if(editTexts.size()==realPosition) {
                        editTexts.add(realPosition, holder.editText);
                    }
                    else {
                        editTexts.remove(realPosition);
                        editTexts.add(realPosition, holder.editText);
                    }

                }

                if(holder.editText!=null) {
                    holder.editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                    && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                                if(listNote.getUncheckedItems().size() < realPosition)
                                    return true;
                                listNote.getUncheckedItems().add(realPosition + 1, "");
                                update();
                                requestFocusTo(realPosition+1);
                                return true;
                            }
                            return false;
                        }
                    });

                   holder.editText.setOnKeyListener(new View.OnKeyListener()
                    {
                        public boolean onKey(View v, int keyCode, KeyEvent event)
                        {
                            if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                    (keyCode == KeyEvent.KEYCODE_DEL))
                            {
                                if(listNote.getUncheckedItems().size() < position)
                                    return false;
                                if(listNote.getUncheckedItems().get(realPosition).length()==0){
                                    listNote.getUncheckedItems().remove(realPosition);
                                    update();
                                    requestFocusTo(realPosition-1);
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                    );
                }

                if(realPosition > listNote.getUncheckedItems().size())
                {
                    holder.editTextListener.updatePosition(realPosition);
                    String s = (listNote.getCheckedItems().get(realPosition - (listNote.getUncheckedItems().size()+1)));
                    holder.editText.setText(s);
                    holder.editText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.editText.setAlpha(0.5f);
                    holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if(b) {
                                holder.button.setVisibility(VISIBLE);
                                holder.editText.setSelection(holder.editText.getText().length());
                            }
                            else
                                holder.button.setVisibility(View.INVISIBLE);
                        }
                    });



                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listNote.getCheckedItems().remove(realPosition - (listNote.getUncheckedItems().size()+1));
                            update();
                            requestFocusLast();
                        }
                    });
                    holder.checkBoxListener.updatePosition(realPosition);
                    onBind = true;
                    holder.checkBox.setChecked(true);
                    onBind = false;
                    System.out.println(position + " - " + listNote.getCheckedItems().get(realPosition - (listNote.getUncheckedItems().size()+1)));
                }

                break;
            case 1:
                if(listNote.getCheckedItems().size()==0){
                    holder.deliver.setVisibility(GONE);
                }
                else{
                    holder.deliver.setVisibility(VISIBLE);
                }
                break;
            case 2:
        }
    }

    public void requestFocusLast(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(editTexts.size()>0)
                editTexts.get(editTexts.size()-1).requestFocus();
            }
        }, 10);
    }

    public void setFocusToEditText(){
        if(listNote.getUncheckedItems().size()==0){
            listNote.getUncheckedItems().add("");
            requestFocusTo(0);
            update();
        } else
            editTexts.get(0).requestFocus();
    }

    public void requestFocusTo(final int position){

        if(position<0)
            return;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(editTexts.size()>0)
                    editTexts.get(position).requestFocus();
            }
        }, 1);
    }

    private void update(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return 2;
        if(position == listNote.getUncheckedItems().size()+1)
            return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return listNote.getUncheckedItems().size() + listNote.getCheckedItems().size()+2;
    }

    private class EditTextListener implements TextWatcher  {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(position< listNote.getUncheckedItems().size())
                listNote.getUncheckedItems().set(position, charSequence.toString());
            if(position > listNote.getUncheckedItems().size())
                listNote.getCheckedItems().set(position - (listNote.getUncheckedItems().size() + 1), charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class HeadLineEditTextListener implements TextWatcher  {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            listNote.setHeadLine(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

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
                    listNote.getCheckedItems().add(listNote.getUncheckedItems(position));
                    listNote.getUncheckedItems().remove(position);
                    editTexts.remove(position);
                    requestFocusTo(position-1);
                    mActivity.changeMenuItemVisibility(listNote.getCheckedItems().size());
                    update();
                } else {
                    listNote.getUncheckedItems().add(listNote.getCheckedItem(position - (listNote.getUncheckedItems().size() + 1)));
                    listNote.getCheckedItems().remove(position - (listNote.getUncheckedItems().size()));
                    update();
                    mActivity.changeMenuItemVisibility(listNote.getCheckedItems().size());
                    requestFocusLast();
                }
            }
        }
    }
}
