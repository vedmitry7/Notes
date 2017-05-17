package com.example.dmitryvedmed.taskbook.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class ListTaskItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean wasMoved = false;
    private boolean canMovement = true;

    public boolean isCanMovement() {
        return canMovement;
    }

    public void setCanMovement(boolean canMovement) {
        this.canMovement = canMovement;
    }

    public ListTaskItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (isCanMovement()) {
          /*  if (recyclerView.getLayoutManager() instanceof GridLayoutManager || recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                //不需要滑动
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }*/

            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            System.out.println("    getMovementFlags ");
            return makeMovementFlags(dragFlags, 0);
        } else {
            return 0;
        }
    }
   /* @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            ViewCompat.setElevation(viewHolder.itemView, 10);
    }*/


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        System.out.println("ON Move - ");
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        wasMoved = true;
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        System.out.println("ON Swipe - " + i);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        System.out.println(" onSelectedChanged " + actionState);
        if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
            System.out.println("        ItemTouchHelper.ACTION_STATE_DRAG");
            wasMoved = false;
        }

        if(actionState==ItemTouchHelper.ACTION_STATE_IDLE){
            System.out.println("        ItemTouchHelper.ACTION_STATE_DRAG");
            if(!wasMoved){
                mAdapter.onItemSelected();
            }
        }

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        System.out.println(" clearView ");
        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }
}
