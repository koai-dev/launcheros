package com.twt.launcheros.utils.widgets.swipeLayout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class SwipeBackLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    companion object{
        const val MIN_FLING_VELOCITY = 400
        const val DEFAULT_SCRIM_COLOR = 0x99000000
        const val FULL_ALPHA = 255
        const val EDGE_LEFT =

    }
}