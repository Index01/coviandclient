package com.covilyfe.v1r0

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToFollowCallback (context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
    private val background = ColorDrawable()
    //private val backgroundColor = Color.parseColor("#f44336")
    private val backgroundColor = Color.BLUE
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean{
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        background.color = backgroundColor
        background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        val p = Paint()
        p.setColor(Color.WHITE)
        p.textSize = 200F
        //background.draw(c)
        c.drawText("Add to friends", dX, dY, p)


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
