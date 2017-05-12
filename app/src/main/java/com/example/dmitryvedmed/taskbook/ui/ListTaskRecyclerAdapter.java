package com.example.dmitryvedmed.taskbook.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.dmitryvedmed.taskbook.logic.ListTask;
import com.example.dmitryvedmed.taskbook.untils.Constants;
import com.example.dmitryvedmed.taskbook.untils.SingletonFonts;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ListTaskRecyclerAdapter extends RecyclerView.Adapter<ListTaskRecyclerAdapter.RecyclerViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private ListTask listTask;
    private boolean onBind;
    private List<EditText> editTexts;
    private List<EditText> checkedTaskEditTexts;
    private int hasInsertInside = -1;
    private ListTaskActivity activity;
    private int fromPos, toPos;
    private SharedPreferences sharedPreferences;

    public ListTaskRecyclerAdapter(ListTask listTask, Context context) {
        this.context = context;
        activity = (ListTaskActivity)context;
        this.listTask = listTask;
        if(listTask.getId() == -1) {
            listTask.setHeadLine("");
            listTask.getUncheckedTasks().add("");
        }
        editTexts = new ArrayList<>();
        checkedTaskEditTexts = new ArrayList<>();

        sharedPreferences = activity.getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE);
    }

    public ListTask getListTask() {
        return listTask;
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Log.d("TAG", " FROM P - " + fromPosition + " TO P - " + toPosition);
        activity.setItemMovement(false);

        int realFromPos = fromPosition-1;
        int realToPos = toPosition -1;

        if(toPosition==0)
            return;

        if(fromPosition>listTask.getUncheckedTasks().size()+1){
            int fromP = realFromPos - (listTask.getUncheckedTasks().size()+1);
            int toP = realToPos - (listTask.getUncheckedTasks().size()+1);
            Log.d("TAG", " NEW       FROM P - " + fromP + " TO P - " + toP);
            if(toP<0) {
                return;
            }
            String prev = listTask.getCheckedTasks().remove(fromP);
            listTask.getCheckedTasks().add(toP, prev);
            notifyItemMoved(fromPosition, toPosition);
            notifyItemChanged(fromPosition);
            notifyItemChanged(toPosition);
            return;
        }

        if(toPosition>=listTask.getUncheckedTasks().size()+1)
            return;

        String prev = listTask.getUncheckedTasks().remove(realFromPos);
        listTask.getUncheckedTasks().add(realToPos, prev);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(fromPosition);
        notifyItemChanged(toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    void deleteCheckedTasks() {
        listTask.getCheckedTasks().clear();
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
            System.out.println("ViewHolder constructor");
            System.out.println("TYPE = " + this.getItemViewType());
            ///????
            editTextListener = new EditTextListener();
            checkBoxListener = new CheckBoxListener();

            deliver =  itemView.findViewById(R.id.deliver_rec);
            if(deliver == null)
                Log.d("TAG", "      DELIVER NUUUUUL" );
            else{
                Log.d("TAG", "      DELIVER NOT NUUUUUL" );
            }

            switch (getItemViewType()){
                case 0:
                    break;
                case 1:
                    break;
            }

            editText = (EditText) itemView.findViewById(R.id.itemListEditText);
            if(editText!=null) {
                editText.setTextSize(sharedPreferences.getInt("taskFontSize", 16));
                editText.setTypeface(SingletonFonts.getInstance(context).getRobotoRegular());
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
                newPoint.setTypeface(SingletonFonts.getInstance(context).getRobotoRegular());
                newPoint.setTextSize(sharedPreferences.getInt("taskFontSize", 16));
            }

            headLineEditText = (EditText) itemView.findViewById(R.id.listHeadEditText2);
            if(headLineEditText!=null) {
                headLineEditText.setTextSize(sharedPreferences.getInt("taskFontSize", 16));
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
        public void onItemClear() {
            Log.d("TAG", "      CLEAR" );
            notifyItemChanged(fromPos);
            notifyItemChanged(toPos);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("TAG", "      ON TOUCH " + motionEvent.getAction() );
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    Log.d("TAG", "      DOWN" );
                    activity.setItemMovement(true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d("TAG", "      UP" );
                    //update();
                    break;
            }
            return false;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TAG", "                                          onCreateViewHolder" );
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
                        Log.d("TAG", "click!!!!!!!!!!!!!!!!!" );

                        listTask.getUncheckedTasks().add("");
                        Log.d("TAG", "Edit texts size = " + editTexts.size() );
                        //activity.scroll(listTask.getUncheckedTasks().size()-1);
                        update();
                        //requestFocusTo(listTask.getUncheckedTasks().size()-1);
                        requestFocusLast();

                       /* for (int i = listTask.getUncheckedTasks().size(); i < getItemCount(); i++) {
                            notifyItemChanged(i);
                        }*/

                        // notifyItemInserted(listTask.getUncheckedTasks().size());
                        //notifyItemChanged(listTask.getUncheckedTasks().size());
                        //notifyItemChanged(listTask.getUncheckedTasks().size()+1);
                        //notifyItemRangeChanged(listTask.getUncheckedTasks().size()+1,getItemCount()-listTask.getUncheckedTasks().size());
                    }
                });
            break;
            case 2:
               View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_line_list_task_recycler_item, parent,false);
                recyclerViewHolder = new RecyclerViewHolder(view2);
            break;

        }
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        Log.d("TAG", "ON BIND VIEW HOLDER, TYPE = " + holder.getItemViewType() );
        String type = holder.getItemViewType() == 0 ? "editText":"button";
        Log.d("TAG", "POSITION -" + position + "," +" TYPE -" +  type );



        if(holder.headLineEditText!=null) {
            holder.headLineEditText.setText(listTask.getHeadLine());
            holder.headLineEditText.setSelection(holder.headLineEditText.getText().length());
        }

        switch (holder.getItemViewType()){
            case 0:
                final int realPosition = position - 1;
                if(realPosition < listTask.getUncheckedTasks().size()) {
                    holder.editTextListener.updatePosition(realPosition);
                    holder.editText.setText(listTask.getUncheckedTasks().get(realPosition));
                    holder.checkBoxListener.updatePosition(realPosition);

                    onBind = true;
                    holder.checkBox.setChecked(false);
                    onBind = false;

                    // holder.editText.setMovementMethod();

                    holder.editText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
                    holder.editText.setAlpha(1f);

                    holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            Log.d("TAG", "ET " + position + " FOCUS " + b );
                            if(b) {
                                holder.button.setVisibility(VISIBLE);
                                holder.editText.setSelection(holder.editText.getText().length());
                            }
                            else {
                                holder.button.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

        /*    if(position==listTask.getUncheckedTasks().size()-1) {

              //  Log.d("TAG", "ET requestFocus for " + position  );
        //        holder.editText.requestFocus();
                holder.button.setVisibility(View.VISIBLE);
            } else {
                holder.button.setVisibility(View.INVISIBLE);
            }
*/
                    holder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listTask.getUncheckedTasks().remove(realPosition);
                            editTexts.remove(realPosition);
                            Log.d("TAG", "Edit texts remove = " + position );
                            Log.d("TAG", "Edit texts size = " + editTexts.size() );
                            update();
                            requestFocusTo(realPosition-1);
                            //notifyItemChanged(position);
                        }
                    });

//              !!!

                    if(editTexts.size()==realPosition) {
                        editTexts.add(realPosition, holder.editText);
                        Log.d("TAG", "Edit texts add = " + position );
                    }
                    else {
                        editTexts.remove(realPosition);
                        editTexts.add(realPosition, holder.editText);
                        Log.d("TAG", "Edit texts remove and add = " + position );
                    }


                    Log.d("TAG", "Edit texts size = " + editTexts.size() );
                }

                if(holder.editText!=null) {
                    holder.editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if( keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                                    && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                                if(listTask.getUncheckedTasks().size() < realPosition)
                                    return true;
                                Log.d("TAG", "click ENTER Add " + position );
                                listTask.getUncheckedTasks().add(realPosition + 1, "");
                                update();
                                //notifyItemChanged(position+1);
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
                                if(listTask.getUncheckedTasks().size() < position)
                                    return false;
                                if(listTask.getUncheckedTasks().get(realPosition).length()==0){
                                    listTask.getUncheckedTasks().remove(realPosition);
                                    //notifyItemRemoved(position);
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

                if(realPosition > listTask.getUncheckedTasks().size())
                {
                    holder.editTextListener.updatePosition(realPosition);
                    String s = (listTask.getCheckedTasks().get(realPosition - (listTask.getUncheckedTasks().size()+1)));
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
                            Log.d("TAG", " BUTTON          CLICK    " + position );
                            listTask.getCheckedTasks().remove(realPosition - (listTask.getUncheckedTasks().size()+1));
                            update();
                            requestFocusLast();
                        }
                    });
                    holder.checkBoxListener.updatePosition(realPosition);
                    onBind = true;
                    holder.checkBox.setChecked(true);
                    onBind = false;
                    System.out.println(position + " - " + listTask.getCheckedTasks().get(realPosition - (listTask.getUncheckedTasks().size()+1)));
               /*     if(position>editTexts.size())
                        return;
                    if(editTexts.size() < position) {
                        editTexts.add(position, holder.editText);
                        Log.d("TAG", "Edit texts add = " + position );
                    }
                    else {
                        editTexts.remove(position);
                        editTexts.add(position, holder.editText);
                        Log.d("TAG", "Edit texts remove and add = " + position );
                    }*/

                    Log.d("TAG", "Edit texts size = " + editTexts.size() );
                }


                break;
            case 1:
                Log.d("TAG", "BUTTTON AND DELIVER" );
                if(listTask.getCheckedTasks().size()==0){
                    holder.deliver.setVisibility(GONE);
                    Log.d("TAG", "UNC = 0" );
                    Log.d("TAG", "VISIBLE GONE" );
                }
                else{
                    holder.deliver.setVisibility(VISIBLE);
                    Log.d("TAG", "UNC != 0" );
                    Log.d("TAG", "VISIBLE TRUE" );
                }
                break;
            case 2:

        }

    }

    public void requestFocusLast(){
        Log.d("TAG", "                                                                                                         requestFocusLast " );
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("TAG", "delay " );
                Log.d("TAG", "requestLastFocus" );
                Log.d("TAG", "focus to " + (editTexts.size()-1) );
                if(editTexts.size()>0)
                editTexts.get(editTexts.size()-1).requestFocus();
            }
        }, 10);
    }

    public void setFocusToEditText(){
        Log.d("TAG", "                                                                                                          setFocusToEditText() " );
        Log.d("TAG", "UNCECKED TASK SIZE = " + listTask.getUncheckedTasks().size() );
        if(listTask.getUncheckedTasks().size()==0){
            listTask.getUncheckedTasks().add("");
            requestFocusTo(0);
            update();
        } else
            editTexts.get(0).requestFocus();
    }

    public void requestFocusTo(final int position){
        Log.d("TAG", "                                                                                                         requestFocusTo " + position );

        if(position<0)
            return;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("TAG", "delay " );
                Log.d("TAG", "requestLastTo " + position + " eDIT TEXT SIZE = " + editTexts.size());
                Log.d("TAG", "requestLastTo " + position );
         /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!      if(position>=editTexts.size()){
                    requestFocusLast();
                    return;
                }*/
                if(editTexts.size()>0)
                    editTexts.get(position).requestFocus();
            }
        }, 1);
    }

    private void update(){
        Log.d("TAG", "update " );
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return 2;
        if(position == listTask.getUncheckedTasks().size()+1)
            return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return listTask.getUncheckedTasks().size() + listTask.getCheckedTasks().size()+2;
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
            if(position<listTask.getUncheckedTasks().size())
                listTask.getUncheckedTasks().set(position, charSequence.toString());
            if(position > listTask.getUncheckedTasks().size())
                listTask.getCheckedTasks().set(position - (listTask.getUncheckedTasks().size() + 1), charSequence.toString());
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
            listTask.setHeadLine(charSequence.toString());
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
                    listTask.getCheckedTasks().add(listTask.getUncheckedTask(position));
                    listTask.getUncheckedTasks().remove(position);
                    editTexts.remove(position);
                    requestFocusTo(position-1);
                    activity.changeMenuItemVisibility(listTask.getCheckedTasks().size());
                    update();
                } else {
                    listTask.getUncheckedTasks().add(listTask.getCheckedTask(position - (listTask.getUncheckedTasks().size() + 1)));
                    listTask.getCheckedTasks().remove(position - (listTask.getUncheckedTasks().size()));
                    update();
                    activity.changeMenuItemVisibility(listTask.getCheckedTasks().size());
                    requestFocusLast();
                }
            }
        }
    }


}
