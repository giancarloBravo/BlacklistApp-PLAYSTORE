package pe.com.gianbravo.blockedcontacts.presentation.view.touchHelper

import android.graphics.Canvas
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import gianca.mostradorropa.friendssApp.touchHelper.ItemTouchHelperAdapter
import gianca.mostradorropa.friendssApp.touchHelper.ItemTouchHelperViewHolder

class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    var isEnabled: Boolean = true

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() is GridLayoutManager) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            return makeMovementFlags(dragFlags, swipeFlags)
        } else {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }
    }

    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (source.getItemViewType() !== target.getItemViewType() || !isEnabled) {
            return false
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition())
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        // Notify the adapter of the dismissal
        if (isEnabled)
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition())
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            val alpha: Float = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.getWidth()
            viewHolder.itemView.setAlpha(alpha)
            viewHolder.itemView.setTranslationX(dX)
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
                itemViewHolder.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.setAlpha(ALPHA_FULL)

        if (viewHolder is ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
    }

    companion object {

        val ALPHA_FULL = 1.0f
    }
}
