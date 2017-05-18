package com.example.dmitryvedmed.taskbook.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to react to {@link
 * ItemTouchHelperAdapter} callbacksинструкция по применению and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private boolean wasMoved = false;
    private boolean canMovement = true;
    private ItemTouchHelperViewHolder itemViewHolder;

    public boolean isCanMovement() {
        return canMovement;
    }

    public void setCanMovement(boolean canMovement) {
        this.canMovement = canMovement;
    }

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
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
            if(isCanMovement()) {
                if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    System.out.println("    GridLayoutManager ");
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                System.out.println("    getMovementFlags ");
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
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
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            System.out.println("        ItemTouchHelper.ACTION_STATE_DRAG");
            if(viewHolder!=null)
            itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            wasMoved = false;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            wasMoved = true;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_IDLE){
            System.out.println("        ItemTouchHelper.ACTION_STATE_IDLE");
            if(!wasMoved){
                mAdapter.onItemSelected();
                itemViewHolder.onItemSelected2();
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