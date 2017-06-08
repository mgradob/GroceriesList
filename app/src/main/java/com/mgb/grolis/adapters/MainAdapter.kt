package com.mgb.grolis.adapters

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mgb.grolis.R
import com.mgb.grolis.models.ItemModel
import com.mgb.grolis.utils.ctx
import kotlinx.android.synthetic.main.list_item_header_main.view.*
import kotlinx.android.synthetic.main.list_item_item_main.view.*

/**
 * Created by mgradob on 6/7/17.
 */
class MainAdapter(val mItems: List<ItemModel>, val onCheckedListener: (ItemModel, Boolean) -> Unit, val onClickedListener: (ItemModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun getItemCount(): Int = if (hasHeaders()) mItems.size + 2 else mItems.size

    override fun getItemViewType(position: Int): Int {
        if (!hasHeaders()) return TYPE_ITEM

        return when (position) {
            0, getDoneItemsStart() -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    private fun hasHeaders(): Boolean {
        if (mItems.size < 2) return false

        var hasUndone = false
        for (item in mItems)
            if (!item.done) {
                hasUndone = true
                break
            }

        var hasDone = false
        for (item in mItems)
            if (item.done) {
                hasDone = true
                break
            }

        return hasUndone && hasDone
    }

    private fun getDoneItemsStart(): Int {
        for (item in mItems) if (item.done) return mItems.indexOf(item) + 1

        return if (mItems.isNotEmpty()) mItems.size + 1 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(layoutInflater.inflate(R.layout.list_item_header_main, parent, false))
            TYPE_ITEM -> ItemViewHolder(layoutInflater.inflate(R.layout.list_item_item_main, parent, false), onCheckedListener, onClickedListener)
            else -> ItemViewHolder(layoutInflater.inflate(R.layout.list_item_item_main, parent, false), onCheckedListener, onClickedListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(position)
            is ItemViewHolder -> holder.bind(mItems[getItemPosition(position)])
        }
    }

    private fun getItemPosition(position: Int): Int {
        if (!hasHeaders()) return position

        if (position == 0) return -1

        val doneStart = getDoneItemsStart()
        return when {
            position == doneStart -> -1
            position > doneStart -> position - 2
            else -> position - 1
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int) {
            itemView.mHeader.text = when(position) {
                0 ->  itemView.ctx.getString(R.string.new_item)
                else -> itemView.ctx.getString(R.string.completed)
            }
        }
    }

    class ItemViewHolder(view: View, val onCheckedListener: (ItemModel, Boolean) -> Unit, val onClickedListener: (ItemModel) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(item: ItemModel) {
            with(item) {
                itemView.mName.text = name
                itemView.mName.isChecked = done
                if (done) itemView.mName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                itemView.mName.setOnCheckedChangeListener { button, checked -> onCheckedListener(this, checked) }

                itemView.mNotes.text = "Quantity: $quantity ${if (notes.isEmpty()) "" else "| $notes"}"

                itemView.setOnClickListener { onClickedListener(this) }
            }
        }
    }
}