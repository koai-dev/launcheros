package com.twt.launcheros.utils.widgets.swipeLayout

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.twt.launcheros.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@SuppressLint("Recycle")
class SwipeBackLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, def: Int = 0
) : ConstraintLayout(context, attrs, def) {
    companion object {
        private const val MIN_FLING_VELOCITY = 400
        private const val DEFAULT_SCRIM_COLOR = 0x99000000
        private const val FULL_ALPHA = 255
        const val EDGE_LEFT = ViewDragHelper.EDGE_LEFT
        const val EDGE_RIGHT = ViewDragHelper.EDGE_RIGHT
        const val EDGE_TOP = ViewDragHelper.EDGE_TOP
        const val EDGE_BOTTOM = ViewDragHelper.EDGE_BOTTOM
        const val EDGE_ALL = EDGE_LEFT or EDGE_RIGHT or EDGE_BOTTOM
        const val STATE_IDLE = ViewDragHelper.STATE_IDLE
        const val STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING
        const val STATE_SETTLING = ViewDragHelper.STATE_SETTLING

        private const val DEFAULT_SCROLL_THRESHOLD = 0.3f
        private const val OVERSCROLL_DISTANCE = 10f
        private val EDGE_FLAGS = arrayOf(EDGE_LEFT, EDGE_RIGHT, EDGE_BOTTOM, EDGE_ALL)
    }

    private var mEdgeFlag: Int? = null
    private var mScrollThreshold = DEFAULT_SCROLL_THRESHOLD
    private var mEnable = true
    private var mContentView: View? = null
    private val mDragHelper = ViewDragHelper.create(this, ViewDragCallback())

    private var mScrollPercent = 0f
    private var mContentLeft = 0
    private var mContentTop = 0
    private var mListeners: ArrayList<SwipeListener> = arrayListOf()
    private var mShadowLeft: Drawable? = null
    private var mShadowRight: Drawable? = null
    private var mShadowBottom: Drawable? = null
    private var mScrimOpacity: Float? = null
    private var mScrimColor = DEFAULT_SCRIM_COLOR
    private var mInLayout: Boolean = true
    private val mTmpRect = Rect()
    private var mTrackingEdge = EDGE_ALL

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SwipeBackLayout, def, R.style.SwipeBackLayout
        )
        val edgeSize = a.getDimensionPixelSize(R.styleable.SwipeBackLayout_edge_size, -1)
        if (edgeSize > 0) setEdgeSize(edgeSize)
        val mode = EDGE_FLAGS[a.getInt(R.styleable.SwipeBackLayout_edge_flag, 0)]
        setEdgeTrackingEnabled(mode)

        val shadowLeft = a.getResourceId(
            R.styleable.SwipeBackLayout_shadow_left, R.drawable.shadow_left
        )
        val shadowRight = a.getResourceId(
            R.styleable.SwipeBackLayout_shadow_right, R.drawable.shadow_right
        )
        val shadowBottom = a.getResourceId(
            R.styleable.SwipeBackLayout_shadow_bottom, R.drawable.shadow_bottom
        )
        setShadow(shadowLeft, EDGE_LEFT)
        setShadow(shadowRight, EDGE_RIGHT)
        setShadow(shadowBottom, EDGE_BOTTOM)
        a.recycle()
        val density = resources.displayMetrics.density
        val minVel = MIN_FLING_VELOCITY * density
        mDragHelper.mMinVelocity = (minVel)
        mDragHelper.mMaxVelocity = (minVel * 2f)
    }

    fun setEdgeSize(size: Int) {
        mDragHelper.mEdgeSize = size
    }

    fun setEdgeTrackingEnabled(edgeFlags: Int) {
        mEdgeFlag = edgeFlags
        mDragHelper.mTrackingEdges = mEdgeFlag ?: edgeFlags
    }

    fun setShadow(shadow: Drawable, edgeFlag: Int) {
        if ((edgeFlag and EDGE_LEFT) != 0) {
            mShadowLeft = shadow
        } else if ((edgeFlag and EDGE_RIGHT) != 0) {
            mShadowRight = shadow
        } else if ((edgeFlag and EDGE_BOTTOM) != 0) {
            mShadowBottom = shadow
        }
        invalidate()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setShadow(resId: Int, edgeFlag: Int) {
        setShadow(resources.getDrawable(resId, resources.newTheme()), edgeFlag)
    }

    fun setContentView(view: View) {
        mContentView = view
    }

    fun setEnableGesture(enable: Boolean) {
        mEnable = enable
    }

    fun setScrimColor(color: Int) {
        mScrimColor = color.toLong()
        invalidate()
    }

    fun addSwipeListener(listener: SwipeListener?) {
        listener?.let { mListeners.add(it) }
    }

    fun removeSwipeListener(listener: SwipeListener?) {
        mListeners.remove(listener)
    }

    fun setScrollThreshold(threshold: Float) {
        require(!(threshold >= 1.0f || threshold <= 0)) { "Threshold value should be between 0 and 1.0" }
        mScrollThreshold = threshold
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (!mEnable) {
                return false
            }
            return try {
                mDragHelper.shouldInterceptTouchEvent(event)
            } catch (e: ArrayIndexOutOfBoundsException) {
                false
            }
        } ?: return super.onInterceptTouchEvent(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (!mEnable) {
                return false
            }
            mDragHelper.processTouchEvent(event)
            return true
        } ?: return super.onTouchEvent(null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mInLayout = true
        mContentView?.layout(
            mContentLeft,
            mContentTop,
            mContentLeft + (mContentView?.measuredWidth ?: 0),
            mContentTop + (mContentView?.measuredHeight ?: 0)
        )
        mInLayout = false
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        val drawContent = child == mContentView

        val ret = super.drawChild(canvas, child, drawingTime)
        if (mScrimOpacity!! > 0 && drawContent && mDragHelper.mDragState != ViewDragHelper.STATE_IDLE) {
            child?.let {
                drawShadow(canvas, it)
                drawScrim(canvas, it)
            }
        }
        return ret
    }

    private fun drawScrim(canvas: Canvas, child: View) {
        val baseAlpha = ((mScrimColor and 0xff000000L) ushr 24).toInt()
        val alpha = (baseAlpha * mScrimOpacity!!).toInt()
        val color = ((alpha shl 24).toLong() or (mScrimColor and 0xffffffL)).toInt()

        if ((mTrackingEdge and EDGE_LEFT) != 0) {
            canvas.clipRect(0, 0, child.left, height)
        } else if ((mTrackingEdge and EDGE_RIGHT) != 0) {
            canvas.clipRect(child.right, 0, right, height)
        } else if ((mTrackingEdge and EDGE_BOTTOM) != 0) {
            canvas.clipRect(child.left, child.bottom, right, height)
        }
        canvas.drawColor(color)
    }

    private fun drawShadow(canvas: Canvas, child: View) {
        val childRect = mTmpRect
        child.getHitRect(childRect)

        if (((mEdgeFlag ?: 0) and EDGE_LEFT) != 0) {
            mShadowLeft?.setBounds(
                childRect.left - (mShadowLeft?.intrinsicWidth ?: 0),
                childRect.top,
                childRect.left,
                childRect.bottom
            )
            mShadowLeft?.alpha = ((mScrimOpacity ?: 1f) * FULL_ALPHA).toInt()
            mShadowLeft?.draw(canvas)
        }

        if (((mEdgeFlag ?: 0) and EDGE_RIGHT) != 0) {
            mShadowRight?.setBounds(
                childRect.right,
                childRect.top,
                childRect.right + (mShadowRight?.intrinsicWidth ?: 0),
                childRect.bottom
            )
            mShadowRight?.alpha = ((mScrimOpacity ?: 1f) * FULL_ALPHA).toInt()
            mShadowRight?.draw(canvas)
        }

        if (((mEdgeFlag ?: 0) and EDGE_BOTTOM) != 0) {
            mShadowBottom?.setBounds(
                childRect.left,
                childRect.bottom,
                childRect.right,
                childRect.bottom + (mShadowBottom?.intrinsicHeight ?: 0)
            )
            mShadowBottom?.alpha = ((mScrimOpacity ?: 1f) * FULL_ALPHA).toInt()
            mShadowBottom?.draw(canvas)
        }
    }

    override fun computeScroll() {
        mScrimOpacity = 1 - mScrollPercent
        if (mDragHelper.continueSettling(true)) {
            this.postInvalidateOnAnimation()
        }
    }

    interface SwipeListener {
        fun onScrollStateChange(state: Int, scrollPercent: Float)
        fun onEdgeTouch(edgeFlag: Int)
        fun onScrollOverThreshold()
    }

    interface SwipeListenerEx : SwipeListener {
        fun onContentViewSwipedBack()
    }

    inner class ViewDragCallback  : ViewDragHelper.Callback {
        private var mIsScrollOverValid = false
        override fun onViewDragStateChanged(state: Int) {
            if (mListeners.isNotEmpty()) {
                for (listener in mListeners) {
                    listener.onScrollStateChange(state, mScrollPercent)
                }
            }
        }

        override fun onViewPositionChanged(
            changedView: View, left: Int, top: Int, dx: Int, dy: Int
        ) {
            mContentView?.let {
                if ((mTrackingEdge and EDGE_LEFT) != 0) {
                    mScrollPercent =
                        abs((attr.left.toFloat() / (mContentView!!.width + mShadowLeft!!.intrinsicWidth)).toDouble())
                            .toFloat()
                } else if ((mTrackingEdge and EDGE_RIGHT) != 0) {
                    mScrollPercent =
                        abs((attr.left.toFloat() / (mContentView!!.width + mShadowRight!!.intrinsicWidth)).toDouble())
                            .toFloat()
                } else if ((mTrackingEdge and EDGE_BOTTOM) != 0) {
                    mScrollPercent =
                        abs((attr.top.toFloat() / (mContentView!!.height + mShadowBottom!!.intrinsicHeight)).toDouble())
                            .toFloat()
                }
            }
            mContentLeft = attr.left
            mContentTop = attr.top
            invalidate()
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true
            }

            if (mListeners.isNotEmpty()) {
                for (listener in mListeners) {
                    listener.onScrollStateChange(mDragHelper.mDragState, mScrollPercent)
                }
            }

            if ( mListeners.isNotEmpty()
                && mDragHelper.mDragState == STATE_DRAGGING && mScrollPercent >= mScrollThreshold && mIsScrollOverValid
            ) {
                mIsScrollOverValid = false
                for (listener in mListeners) {
                    listener.onScrollOverThreshold()
                }
            }

            if (mScrollPercent >= 1) {
                if (mListeners.isNotEmpty()) {
                    for (listener in mListeners) {
                        if (listener is SwipeListenerEx) {
                            listener.onContentViewSwipedBack()
                        }
                    }
                }
            }
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {

        }

        override fun onViewReleased(releasedChild: View, xVel: Float, yVel: Float) {
            val childWidth = releasedChild.width
            val childHeight = releasedChild.height

            var left = 0
            var top = 0
            if ((mTrackingEdge and EDGE_LEFT) != 0) {
                left =
                    if (xVel > 0 || xVel == 0f && mScrollPercent > mScrollThreshold) ((childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE).toInt()) else 0
            } else if ((mTrackingEdge and EDGE_RIGHT) != 0) {
                left =
                    if (xVel < 0 || xVel == 0f && mScrollPercent > mScrollThreshold) (-((childWidth + mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE))).toInt() else 0
            } else if ((mTrackingEdge and EDGE_BOTTOM) != 0) {
                top =
                    if (yVel < 0 || yVel == 0f && mScrollPercent > mScrollThreshold) (-((childHeight + mShadowBottom!!.intrinsicHeight + OVERSCROLL_DISTANCE))).toInt() else 0
            }

            mDragHelper.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val ret = mDragHelper.isEdgeTouched(mEdgeFlag!!, pointerId)
            if (ret) {
                if (mDragHelper.isEdgeTouched(EDGE_LEFT, pointerId)) {
                    mTrackingEdge = EDGE_LEFT
                } else if (mDragHelper.isEdgeTouched(EDGE_RIGHT, pointerId)) {
                    mTrackingEdge = EDGE_RIGHT
                } else if (mDragHelper.isEdgeTouched(EDGE_BOTTOM, pointerId)) {
                    mTrackingEdge = EDGE_BOTTOM
                }
                if (mListeners.isNotEmpty()) {
                    for (listener in mListeners) {
                        listener.onEdgeTouch(mTrackingEdge)
                    }
                }
                mIsScrollOverValid = true
            }
            var directionCheck = false
            if (mEdgeFlag == EDGE_LEFT || mEdgeFlag == EDGE_RIGHT) {
                directionCheck = !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, pointerId)
            } else if (mEdgeFlag == EDGE_BOTTOM) {
                directionCheck = !mDragHelper
                    .checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId)
            } else if (mEdgeFlag == androidx.customview.widget.ViewDragHelper.EDGE_ALL) {
                directionCheck = true
            }
            return ret and directionCheck
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            var ret = 0
            if ((mTrackingEdge and EDGE_BOTTOM) != 0) {
                ret = min(0, max(attr.top, -child.height))
            }
            return ret
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var ret = 0
            if ((mTrackingEdge and EDGE_LEFT) != 0) {
                ret = child.width.coerceAtMost(max(attr.left, 0))
            } else if ((mTrackingEdge and EDGE_RIGHT) != 0) {
                ret = min(0, max(attr.left, -child.width))
            }
            return ret
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return mEdgeFlag!! and (EDGE_LEFT or EDGE_RIGHT)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mEdgeFlag!! and EDGE_BOTTOM
        }
    }
}