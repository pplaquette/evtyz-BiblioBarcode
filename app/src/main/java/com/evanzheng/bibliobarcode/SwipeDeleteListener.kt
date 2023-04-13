package com.evanzheng.bibliobarcode

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// implementation of "SwipeToDeleteCallback", written by Zachery Osborn at https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
internal abstract class SwipeDeleteListener(calledAdapter: BibliographyAdapter?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        a: RecyclerView.ViewHolder,
        b: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}