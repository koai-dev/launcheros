package com.twt.launcheros.utils.widgets.swipeLayout

import android.content.Context
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.Scroller
import java.util.Arrays
import kotlin.math.max
import kotlin.math.min

class ViewDragHelper private constructor(context: Context, forParent: ViewGroup, cb: Callback) {
    companion object {
        const val TAG = "ViewDragHelper"
        const val INVALID_POINTER = -1

        const val STATE_IDLE = 0
        const val STATE_DRAGGING = 1
        const val STATE_SETTLING = 2

        const val EDGE_LEFT = 1 shl 0
        const val EDGE_RIGHT: Int = 1 shl 1
        const val EDGE_TOP: Int = 1 shl 2
        const val EDGE_BOTTOM: Int = 1 shl 3
        const val EDGE_ALL: Int = EDGE_LEFT or EDGE_TOP or EDGE_RIGHT or EDGE_BOTTOM

        const val DIRECTION_HORIZONTAL: Int = 1 shl 0
        const val DIRECTION_VERTICAL: Int = 1 shl 1
        const val DIRECTION_ALL: Int = DIRECTION_HORIZONTAL or DIRECTION_VERTICAL

        const val EDGE_SIZE: Int = 20 // dp
        private const val BASE_SETTLE_DURATION: Int = 256 // ms
        private const val MAX_SETTLE_DURATION: Int = 600 // ms

        fun create(forParent: ViewGroup, cb: Callback, sensitivity: Float? = null): ViewDragHelper {
            return ViewDragHelper(forParent.context, forParent, cb).apply {
                sensitivity?.let { sensitivity ->
                    mTouchSlop *= (1 / sensitivity).toInt()
                }
            }
        }
    }

    private var mParentView: ViewGroup = forParent
    private var mCallback = cb
    var mTouchSlop: Int
    var mMaxVelocity: Float
    var mMinVelocity: Float
    private var mEdgeSize: Int
    private var mScroller: Scroller
    var mTrackingEdges: Int = EDGE_ALL
    var mActivePointerId: Int = INVALID_POINTER
    private var mVelocityTracker: VelocityTracker? = null

    private var mInitialMotionX = arrayOf<Float>()
    private var mInitialMotionY = arrayOf<Float>()
    private var mLastMotionX = arrayOf<Float>()
    private var mLastMotionY = arrayOf<Float>()
    private var mInitialEdgeTouched = arrayOf<Int>()
    private var mEdgeDragsInProgress = arrayOf<Int>()
    private var mEdgeDragsLocked = arrayOf<Int>()
    private var mPointersDown = 0

    private var mCapturedView: View? = null
    var mDragState: Int = STATE_IDLE
        set(value) {
            if (mDragState != value) {
                field = value
                mCallback.onViewDragStateChanged(value)
                if (value == STATE_IDLE) {
                    mCapturedView = null
                }
            }
        }
    private val sInterpolator = Interpolator { input ->
        val t = input - 1f
        t * t * t * t * t + 1.0f
    }

    private val mSetIdleRunnable = Runnable {
        mDragState = STATE_IDLE
    }


    interface Callback {
        fun onViewDragStateChanged(state: Int)
        fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int)
        fun onViewCaptured(capturedChild: View, activePointerId: Int)
        fun onViewReleased(releasedChild: View, xVel: Float, yVel: Float)
        fun onEdgeTouched(edgeFlags: Int, pointerId: Int)
        fun onEdgeLock(edgeFlags: Int): Boolean = false
        fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int)
        fun getOrderedChildIndex(index: Int): Int = index
        fun getViewHorizontalDragRange(child: View): Int = 0
        fun getViewVerticalDragRange(child: View): Int = 0
        fun tryCaptureView(child: View, pointerId: Int): Boolean
        fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = 0
        fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = 0
    }

    init {
        val vc = ViewConfiguration.get(context)
        mTouchSlop = vc.scaledTouchSlop
        mMaxVelocity = vc.scaledMaximumFlingVelocity.toFloat()
        mMinVelocity = vc.scaledMinimumFlingVelocity.toFloat()
        mEdgeSize = (EDGE_SIZE * context.resources.displayMetrics.density + 0.5f).toInt()
        mScroller = Scroller(context, sInterpolator)
    }

    fun setSensitivity(context: Context, sensitivity: Float) {
        val s = max(0f, min(1f, sensitivity))
        val viewConfiguration = ViewConfiguration.get(context)
        mTouchSlop = (viewConfiguration.scaledTouchSlop / s).toInt()
    }

    fun captureChildView(child: View, activePointerId: Int) {
        if (child.parent != mParentView) {
            throw IllegalArgumentException(
                "captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view ($mParentView)"
            )
        }
        mCapturedView = child
        mActivePointerId = activePointerId
        mCallback.onViewCaptured(child, activePointerId)
        mDragState = STATE_DRAGGING
    }

    fun cancel() {
        mActivePointerId = INVALID_POINTER
        clearMotionHistory()

        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }

    }

    fun abort() {
        cancel()
        if (mDragState == STATE_SETTLING) {
            val oldX = mScroller.currX
            val oldY = mScroller.currY
            mScroller.abortAnimation()
            val newX = mScroller.currX
            val newY = mScroller.currY
            mCapturedView?.let {
                mCallback.onViewPositionChanged(
                    it,
                    newX,
                    newY,
                    newX - oldX,
                    newY - oldY
                )
            }
        }
        mDragState = STATE_IDLE
    }

    fun smoothSlideViewTo(child: View, finalLeft: Int, finalTop: Int): Boolean {
        mCapturedView = child
        mActivePointerId = INVALID_POINTER
        return forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0)
    }

    private fun forceSettleCapturedViewAt(finalLeft: Int, finalTop: Int, xVel: Int, yVel: Int): Boolean {
        TODO("Not yet implemented")
    }

    private fun clearMotionHistory() {
        Arrays.fill(mInitialMotionX, 0)
        Arrays.fill(mInitialMotionY, 0)
        Arrays.fill(mLastMotionX, 0)
        Arrays.fill(mLastMotionY, 0)
        Arrays.fill(mInitialEdgeTouched, 0)
        Arrays.fill(mEdgeDragsInProgress, 0)
        Arrays.fill(mEdgeDragsLocked, 0)
        mPointersDown = 0
    }

    private fun clearMotionHistory(pointerId: Int) {
        mInitialMotionX[pointerId] = 0f
        mInitialMotionY[pointerId] = 0f
        mLastMotionX[pointerId] = 0f
        mLastMotionY[pointerId] = 0f
        mInitialEdgeTouched[pointerId] = 0
        mEdgeDragsInProgress[pointerId] = 0
        mEdgeDragsLocked[pointerId] = 0
        mPointersDown = mPointersDown and (1 shl pointerId).inv()
    }

}