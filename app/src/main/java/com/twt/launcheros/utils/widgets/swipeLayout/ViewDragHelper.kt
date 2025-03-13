package com.twt.launcheros.utils.widgets.swipeLayout

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.Scroller
import java.util.Arrays
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin


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
    var mEdgeSize: Int
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
    private var mReleaseInProgress = false

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
        fun onEdgeTouched(edgeFlags: Int, pointerId: Int) = Unit
        fun onEdgeLock(edgeFlags: Int): Boolean = false
        fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) = Unit
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

    fun settleCapturedViewAt(finalLeft: Int, finalTop: Int): Boolean {
        if (!mReleaseInProgress) throw IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased")
        return mVelocityTracker?.let { mVelocityTracker ->
            forceSettleCapturedViewAt(
                finalLeft, finalTop,
                mVelocityTracker.getXVelocity(mActivePointerId).toInt(),
                mVelocityTracker.getYVelocity(mActivePointerId).toInt()
            )
        } ?: false
    }

    fun flingCapturedView(minLeft: Int, minTop: Int, maxLeft: Int, maxTop: Int) {
        if (!mReleaseInProgress) throw IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased")
        mScroller.fling(
            mCapturedView?.left ?: 0,
            mCapturedView?.top ?: 0,
            mVelocityTracker?.getXVelocity(mActivePointerId)?.toInt() ?: 0,
            mVelocityTracker?.getYVelocity(mActivePointerId)?.toInt() ?: 0,
            minLeft,
            maxLeft,
            minTop,
            maxTop
        )
        mDragState = STATE_SETTLING
    }

    fun continueSettling(deferCallbacks: Boolean): Boolean {
        if (mDragState == STATE_SETTLING) {
            var keepGoing = mScroller.computeScrollOffset()
            val x = mScroller.currX
            val y = mScroller.currY
            val dx = x - (mCapturedView?.left ?: 0)
            val dy = y - (mCapturedView?.top ?: 0)
            if (dx != 0) {
                mCapturedView?.offsetLeftAndRight(dx)
            }
            if (dy != 0) {
                mCapturedView?.offsetTopAndBottom(dy)
            }
            if (dx != 0 || dy != 0) {
                mCapturedView?.let { mCapturedView ->
                    mCallback.onViewPositionChanged(mCapturedView, x, y, dx, dy)
                }
            }

            if (keepGoing && x == mScroller.finalX && y == mScroller.finalY) {
                // Close enough. The interpolator/scroller might think we're
                // still moving
                // but the user sure doesn't.
                mScroller.abortAnimation()
                keepGoing = mScroller.isFinished
            }
            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView.post(mSetIdleRunnable)
                } else {
                    mDragState = STATE_IDLE
                }
            }
        }
        return mDragState == STATE_SETTLING
    }

    fun isPointerDown(pointerId: Int): Boolean {
        return (mPointersDown and (1 shl pointerId)) != 0
    }

    fun canScroll(v: View, checkV: Boolean, dx: Int, dy: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = v.childCount
            // Count backwards - let topmost views consume scroll distance
            // first.
            for (i in count - 1 downTo 0) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                val child = v.getChildAt(i)
                if (x + scrollX >= child.left && x + scrollX < child.right && y + scrollY >= child.top && y + scrollY < child.bottom && canScroll(
                        child, true, dx, dy, x + scrollX - child.left, y
                                + scrollY - child.top
                    )
                ) {
                    return true
                }
            }
        }

        return checkV && (v.canScrollHorizontally(-dx) || v.canScrollVertically(-dy))
    }

    fun tryCaptureViewForDrag(toCapture: View, pointerId: Int): Boolean {
        if (toCapture == mCapturedView && mActivePointerId == pointerId) {
            // Already done!
            return true
        }
        if (mCallback.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId
            captureChildView(toCapture, pointerId)
            return true
        }
        return false
    }

    fun shouldInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        val actionIndex = ev.actionIndex

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = ev.getPointerId(0)
                saveInitialMotion(x, y, pointerId)

                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                // Catch a settling view if possible.
                if (toCapture == mCapturedView && mDragState == STATE_SETTLING) {
                    if (toCapture != null) {
                        tryCaptureViewForDrag(toCapture, pointerId)
                    }
                }

                val edgesTouched = mInitialEdgeTouched[pointerId]
                if ((edgesTouched and mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = ev.getPointerId(actionIndex)
                val x = ev.getX(actionIndex)
                val y = ev.getY(actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    val edgesTouched = mInitialEdgeTouched[pointerId]
                    if ((edgesTouched and mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (mDragState == STATE_SETTLING) {
                    // Catch a settling view if possible.
                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    if (toCapture == mCapturedView) {
                        if (toCapture != null) {
                            tryCaptureViewForDrag(toCapture, pointerId)
                        }
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                // First to cross a touch slop over a draggable view wins. Also
                // report edge drags.
                val pointerCount = ev.pointerCount
                var i = 0
                while (i < pointerCount) {
                    val pointerId = ev.getPointerId(i)
                    val x = ev.getX(i)
                    val y = ev.getY(i)
                    val dx = x - mInitialMotionX[pointerId]
                    val dy = y - mInitialMotionY[pointerId]

                    reportNewEdgeDrags(dx, dy, pointerId)
                    if (mDragState == STATE_DRAGGING) {
                        // Callback might have started an edge drag
                        break
                    }

                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy)
                        && tryCaptureViewForDrag(toCapture, pointerId)
                    ) {
                        break
                    }
                    i++
                }
                saveLastMotion(ev)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = ev.getPointerId(actionIndex)
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancel()
            }
        }

        return mDragState == STATE_DRAGGING
    }

    fun findTopChildUnder(x: Int, y: Int): View? {
        val childCount = mParentView.childCount
        for (i in childCount - 1 downTo 0) {
            val child = mParentView.getChildAt(mCallback.getOrderedChildIndex(i))
            if (x >= child.left && x < child.right && y >= child.top && y < child.bottom) {
                return child
            }
        }
        return null
    }

    fun checkTouchSlop(directions: Int): Boolean {
        val count: Int = mInitialMotionX.size
        for (i in 0 until count) {
            if (checkTouchSlop(directions, i)) {
                return true
            }
        }
        return false
    }

    fun checkTouchSlop(directions: Int, pointerId: Int): Boolean {
        if (!isPointerDown(pointerId)) {
            return false
        }

        val checkHorizontal =
            (directions and androidx.customview.widget.ViewDragHelper.DIRECTION_HORIZONTAL) == androidx.customview.widget.ViewDragHelper.DIRECTION_HORIZONTAL
        val checkVertical =
            (directions and androidx.customview.widget.ViewDragHelper.DIRECTION_VERTICAL) == androidx.customview.widget.ViewDragHelper.DIRECTION_VERTICAL

        val dx = mLastMotionX[pointerId] - mInitialMotionX[pointerId]
        val dy = mLastMotionY[pointerId] - mInitialMotionY[pointerId]

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return abs(dx.toDouble()) > mTouchSlop
        } else if (checkVertical) {
            return abs(dy.toDouble()) > mTouchSlop
        }
        return false
    }

    fun processTouchEvent(ev: MotionEvent) {
        val action = ev.actionMasked
        val actionIndex = ev.actionIndex

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = ev.getPointerId(0)
                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                saveInitialMotion(x, y, pointerId)

                // Since the parent is already directly processing this touch
                // event,
                // there is no reason to delay for a slop before dragging.
                // Start immediately if possible.
                toCapture?.let { tryCaptureViewForDrag(it, pointerId) }

                val edgesTouched = mInitialEdgeTouched[pointerId]
                if ((edgesTouched and mTrackingEdges) != 0) {
                    mCallback.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerId = ev.getPointerId(actionIndex)
                val x = ev.getX(actionIndex)
                val y = ev.getY(actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    // If we're idle we can do anything! Treat it like a normal
                    // down event.

                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    toCapture?.let { tryCaptureViewForDrag(it, pointerId) }

                    val edgesTouched = mInitialEdgeTouched[pointerId]
                    if ((edgesTouched and mTrackingEdges) != 0) {
                        mCallback.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (isCapturedViewUnder(x.toInt(), y.toInt())) {
                    // We're still tracking a captured view. If the same view is
                    // under this
                    // point, we'll swap to controlling it with this pointer
                    // instead.
                    // (This will still work if we're "catching" a settling
                    // view.)

                    mCapturedView?.let { tryCaptureViewForDrag(it, pointerId) }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mDragState == STATE_DRAGGING) {
                    val index = ev.findPointerIndex(mActivePointerId)
                    val x = ev.getX(index)
                    val y = ev.getY(index)
                    val idx = (x - mLastMotionX[mActivePointerId]).toInt()
                    val idy = (y - mLastMotionY[mActivePointerId]).toInt()

                    dragTo(
                        mCapturedView?.left?.plus(idx) ?: idx,
                        mCapturedView?.top?.plus(idy) ?: idy, idx, idy
                    )

                    saveLastMotion(ev)
                } else {
                    // Check to see if any pointer is now over a draggable view.
                    val pointerCount = ev.pointerCount
                    var i = 0
                    while (i < pointerCount) {
                        val pointerId = ev.getPointerId(i)
                        val x = ev.getX(i)
                        val y = ev.getY(i)
                        val dx = x - mInitialMotionX[pointerId]
                        val dy = y - mInitialMotionY[pointerId]

                        reportNewEdgeDrags(dx, dy, pointerId)
                        if (mDragState == STATE_DRAGGING) {
                            // Callback might have started an edge drag.
                            break
                        }

                        val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                        if (checkTouchSlop(toCapture, dx, dy)
                            && toCapture?.let { tryCaptureViewForDrag(it, pointerId) } == true
                        ) {
                            break
                        }
                        i++
                    }
                    saveLastMotion(ev)
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = ev.getPointerId(actionIndex)
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    // Try to find another pointer that's still holding on to
                    // the captured view.
                    var newActivePointer = androidx.customview.widget.ViewDragHelper.INVALID_POINTER
                    val pointerCount = ev.pointerCount
                    var i = 0
                    while (i < pointerCount) {
                        val id = ev.getPointerId(i)
                        if (id == mActivePointerId) {
                            // This one's going away, skip.
                            i++
                            continue
                        }

                        val x = ev.getX(i)
                        val y = ev.getY(i)
                        if (findTopChildUnder(x.toInt(), y.toInt()) == mCapturedView
                            && tryCaptureViewForDrag(mCapturedView!!, id)
                        ) {
                            newActivePointer = mActivePointerId
                            break
                        }
                        i++
                    }

                    if (newActivePointer == androidx.customview.widget.ViewDragHelper.INVALID_POINTER) {
                        // We didn't find another pointer still touching the
                        // view, release it.
                        releaseViewForPointerUp()
                    }
                }
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP -> {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp()
                }
                cancel()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mDragState == STATE_DRAGGING) {
                    dispatchViewReleased(0f, 0f)
                }
                cancel()
            }
        }
    }

    fun isCapturedViewUnder(x: Int, y: Int): Boolean {
        return isViewUnder(mCapturedView, x, y)
    }

    fun isViewUnder(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        return x >= view.left && x < view.right && y >= view.top && y < view.bottom
    }

    fun isEdgeTouched(edges: Int): Boolean{
        val count: Int = mInitialEdgeTouched.size
        for (i in 0 until count) {
            if (isEdgeTouched(edges, i)) {
                return true
            }
        }
        return false
    }

    fun isEdgeTouched(edges: Int, pointerId: Int): Boolean {
        return isPointerDown(pointerId) && (mInitialEdgeTouched[pointerId] and edges) != 0
    }

    private fun releaseViewForPointerUp() {
        mVelocityTracker?.computeCurrentVelocity(1000, mMaxVelocity)
        val xvel = clampMag(
            mVelocityTracker?.getXVelocity(mActivePointerId) ?: 0f,
            mMinVelocity, mMaxVelocity
        )
        val yvel = clampMag(
            mVelocityTracker?.getYVelocity(mActivePointerId) ?: 0f,
            mMinVelocity, mMaxVelocity
        )
        dispatchViewReleased(xvel, yvel)
    }

    private fun dragTo(left: Int, top: Int, dx: Int, dy: Int) {
        mCapturedView ?: return
        var clampedX = left
        var clampedY = top
        val oldLeft = mCapturedView?.left ?: 0
        val oldTop = mCapturedView?.top ?: 0
        if (dx != 0) {
            clampedX = mCallback.clampViewPositionHorizontal(mCapturedView!!, left, dx)
            mCapturedView!!.offsetLeftAndRight(clampedX - oldLeft)
        }
        if (dy != 0) {
            clampedY = mCallback.clampViewPositionVertical(mCapturedView!!, top, dy)
            mCapturedView?.offsetTopAndBottom(clampedY - oldTop)
        }

        if (dx != 0 || dy != 0) {
            val clampedDx = clampedX - oldLeft
            val clampedDy = clampedY - oldTop
            mCallback
                .onViewPositionChanged(mCapturedView!!, clampedX, clampedY, clampedDx, clampedDy)
        }
    }

    private fun checkTouchSlop(child: View?, dx: Float, dy: Float): Boolean {
        if (child == null) {
            return false
        }
        val checkHorizontal = mCallback.getViewHorizontalDragRange(child) > 0
        val checkVertical = mCallback.getViewVerticalDragRange(child) > 0

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return abs(dx.toDouble()) > mTouchSlop
        } else if (checkVertical) {
            return abs(dy.toDouble()) > mTouchSlop
        }
        return false
    }

    private fun reportNewEdgeDrags(dx: Float, dy: Float, pointerId: Int) {
        var dragsStarted = 0
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_LEFT)) {
            dragsStarted = dragsStarted or EDGE_LEFT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_TOP)) {
            dragsStarted = dragsStarted or EDGE_TOP
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_RIGHT)) {
            dragsStarted = dragsStarted or EDGE_RIGHT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_BOTTOM)) {
            dragsStarted = dragsStarted or EDGE_BOTTOM
        }

        if (dragsStarted != 0) {
            mEdgeDragsInProgress[pointerId] = mEdgeDragsInProgress[pointerId] or dragsStarted
            mCallback.onEdgeDragStarted(dragsStarted, pointerId)
        }
    }

    private fun checkNewEdgeDrag(delta: Float, odelta: Float, pointerId: Int, edge: Int): Boolean {
        val absDelta = abs(delta.toDouble()).toFloat()
        val absODelta = abs(odelta.toDouble()).toFloat()

        if ((mInitialEdgeTouched[pointerId] and edge) != edge || (mTrackingEdges and edge) == 0 || (mEdgeDragsLocked[pointerId] and edge) == edge || (mEdgeDragsInProgress[pointerId] and edge) == edge || (absDelta <= mTouchSlop && absODelta <= mTouchSlop)) {
            return false
        }
        if (absDelta < absODelta * 0.5f && mCallback.onEdgeLock(edge)) {
            mEdgeDragsLocked[pointerId] = mEdgeDragsLocked[pointerId] or edge
            return false
        }
        return (mEdgeDragsInProgress[pointerId] and edge) == 0 && absDelta > mTouchSlop
    }

    private fun saveLastMotion(ev: MotionEvent) {
        val pointerCount = ev.pointerCount
        for (i in 0 until pointerCount) {
            val pointerId = ev.getPointerId(i)
            val x = ev.getX(i)
            val y = ev.getY(i)
            mLastMotionX[pointerId] = x
            mLastMotionY[pointerId] = y
        }
    }

    private fun saveInitialMotion(x: Float, y: Float, pointerId: Int) {
        ensureMotionHistorySizeForId(pointerId)
        mInitialMotionX[pointerId] = x.also { mLastMotionX[pointerId] = it }.toFloat()
        mInitialMotionY[pointerId] = y.also { mLastMotionY[pointerId] = it }.toFloat()
        mInitialEdgeTouched[pointerId] = getEdgeTouched(x.toInt(), y.toInt())
        mPointersDown = mPointersDown or (1 shl pointerId)
    }

    private fun getEdgeTouched(x: Int, y: Int): Int {
        var result = 0
        if (x < mParentView.left + mEdgeSize) result = EDGE_LEFT
        if (y < mParentView.top + mEdgeSize) result = EDGE_TOP
        if (x > mParentView.right - mEdgeSize) result = EDGE_RIGHT
        if (y > mParentView.bottom - mEdgeSize) result = EDGE_BOTTOM
        return result
    }

    private fun ensureMotionHistorySizeForId(pointerId: Int) {
        if (mInitialMotionX.size <= pointerId) {
            val imx = FloatArray(pointerId + 1).toTypedArray()
            val imy = FloatArray(pointerId + 1).toTypedArray()
            val lmx = FloatArray(pointerId + 1).toTypedArray()
            val lmy = FloatArray(pointerId + 1).toTypedArray()
            val iit = IntArray(pointerId + 1).toTypedArray()
            val edip = IntArray(pointerId + 1).toTypedArray()
            val edl = IntArray(pointerId + 1).toTypedArray()

            System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX.size)
            System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY.size)
            System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX.size)
            System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY.size)
            System.arraycopy(mInitialEdgeTouched, 0, iit, 0, mInitialEdgeTouched.size)
            System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress.size)
            System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked.size)


            mInitialMotionX = imx
            mInitialMotionY = imy
            mLastMotionX = lmx
            mLastMotionY = lmy
            mInitialEdgeTouched = iit
            mEdgeDragsInProgress = edip
            mEdgeDragsLocked = edl
        }
    }

    private fun dispatchViewReleased(xVel: Float, yVel: Float) {
        mReleaseInProgress = true
        mCapturedView?.let { mCallback.onViewReleased(it, xVel, yVel) }
        mReleaseInProgress = false

        if (mDragState == STATE_DRAGGING) {
            // onViewReleased didn't call a method that would have changed this.
            // Go idle.
            mDragState = STATE_IDLE
        }
    }

    private fun forceSettleCapturedViewAt(
        finalLeft: Int,
        finalTop: Int,
        xVel: Int,
        yVel: Int
    ): Boolean {
        mCapturedView?.let { mCapturedView ->
            val startLeft = mCapturedView.left
            val startTop = mCapturedView.top
            val dx = finalLeft - startLeft
            val dy = finalTop - startTop

            if (dx == 0 && dy == 0) {
                // Nothing to do. Send callbacks, be done.
                mScroller.abortAnimation()
                mDragState = STATE_IDLE
                return false
            }

            val duration = computeSettleDuration(mCapturedView, dx, dy, xVel, yVel)
            mScroller.startScroll(startLeft, startTop, dx, dy, duration)

            mDragState = STATE_SETTLING
            return true
        } ?: return false
    }

    private fun clampMag(value: Int, absMin: Int, absMax: Int): Int {
        val absValue = abs(value.toDouble()).toInt()
        if (absValue < absMin) return 0
        if (absValue > absMax) return if (value > 0) absMax else -absMax
        return value
    }

    private fun clampMag(value: Float, absMin: Float, absMax: Float): Float {
        val absValue = abs(value.toDouble()).toFloat()
        if (absValue < absMin) return 0f
        if (absValue > absMax) return if (value > 0) absMax else -absMax
        return value
    }

    private fun computeSettleDuration(child: View, dx: Int, dy: Int, xxvel: Int, yyvel: Int): Int {
        val xvel = clampMag(xxvel, mMinVelocity.toInt(), mMaxVelocity.toInt())
        val yvel = clampMag(yyvel, mMinVelocity.toInt(), mMaxVelocity.toInt())
        val absDx = abs(dx)
        val absDy = abs(dy)
        val absXVel = abs(xvel)
        val absYVel = abs(yvel)
        val addedVel = absXVel + absYVel
        val addedDistance = absDx + absDy

        val xweight =
            if (xvel != 0) (absXVel.toFloat() / addedVel) else (absDx.toFloat() / addedDistance)
        val yweight =
            if (yvel != 0) absYVel.toFloat() / addedVel else absDy.toFloat() / addedDistance

        val xduration = computeAxisDuration(dx, xvel, mCallback.getViewHorizontalDragRange(child));
        val yduration = computeAxisDuration(dy, yvel, mCallback.getViewVerticalDragRange(child));

        return (xduration * xweight + yduration * yweight).toInt()
    }

    private fun computeAxisDuration(delta: Int, velocityF: Int, motionRange: Int): Int {
        var velocity = velocityF
        if (delta == 0) {
            return 0
        }

        val width = mParentView.width
        val halfWidth = width / 2
        val distanceRatio = min(1.0, (abs(delta.toDouble()).toFloat() / width).toDouble())
            .toFloat()
        val distance: Float =
            (halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio))

        val duration: Int
        velocity = abs(velocity.toDouble()).toInt()
        if (velocity > 0) {
            duration = (4 * Math.round(1000 * abs((distance / velocity).toDouble()))).toInt()
        } else {
            val range = abs(delta.toDouble()).toFloat() / motionRange
            duration = ((range + 1) * BASE_SETTLE_DURATION).toInt()
        }
        return duration.coerceAtMost(MAX_SETTLE_DURATION)
    }

    private fun distanceInfluenceForSnapDuration(fX: Float): Float {
        var f = fX
        f -= 0.5f // center the values about 0.
        f *= (0.3f * Math.PI / 2.0f).toFloat()
        return sin(f.toDouble()).toFloat()
    }

    private fun clearMotionHistory() {
        Arrays.fill(mInitialMotionX, 0f)
        Arrays.fill(mInitialMotionY, 0f)
        Arrays.fill(mLastMotionX, 0f)
        Arrays.fill(mLastMotionY, 0f)
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