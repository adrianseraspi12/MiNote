package com.suzei.minote.utils.recycler_view.callback

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveTouchHelper : ItemTouchHelper.Callback() {

    interface MoveCallback {
        fun isInDeleteArea(view: View): Boolean
        fun removeItem(position: Int)
        fun onClearView()
        fun onDrag()
    }

    private var hasRemoveItem = false

    lateinit var callback: MoveCallback

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (isCurrentlyActive.not() && callback.isInDeleteArea(viewHolder.itemView) && !hasRemoveItem) {
            hasRemoveItem = true
            callback.removeItem(viewHolder.adapterPosition)
            viewHolder.itemView.visibility = View.GONE
            return
        }

        viewHolder.itemView.translationY = dY
        viewHolder.itemView.translationX = dX
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or
                ItemTouchHelper.DOWN or
                ItemTouchHelper.START or
                ItemTouchHelper.END
        return makeMovementFlags(dragFlags, 0)

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        callback.onClearView()
        hasRemoveItem = false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        hasRemoveItem = false
        callback.onDrag()
    }

    override fun getAnimationDuration(recyclerView: RecyclerView, animationType: Int, animateDx: Float, animateDy: Float): Long {
        if (hasRemoveItem) return 0
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}