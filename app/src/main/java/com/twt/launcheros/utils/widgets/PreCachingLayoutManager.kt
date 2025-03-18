package com.twt.launcheros.utils.widgets

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class PreCachingLayoutManager(context: Context?, spanCount: Int, reverseLayout: Boolean = false) :
    GridLayoutManager(context, spanCount) {
    private val defaultExtraLayoutSpace = 5000
    private var extraLayoutSpace = -1

    init {
        this.spanCount = spanCount
        this.reverseLayout = reverseLayout
    }

    fun setExtraLayoutSpace(extraLayoutSpace: Int) {
        this.extraLayoutSpace = extraLayoutSpace
    }

    override fun calculateExtraLayoutSpace(
        state: RecyclerView.State,
        extraLayoutSpace: IntArray,
    ) {
//        val extraSpace = (itemCount * 100) // Adjust this value based on your needs
        val extraSpace =
            if (this.extraLayoutSpace > 0) {
                this.extraLayoutSpace
            } else {
                defaultExtraLayoutSpace
            }

        // Fill the extraLayoutSpace array with the calculated values
        extraLayoutSpace[0] = extraSpace // For start space
        extraLayoutSpace[1] = extraSpace // For end space
    }
}
