package com.twt.launcheros.utils.widgets

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.koai.base.utils.LogUtils
import com.twt.launcheros.utils.ScreenUtilsWrapper
import kotlin.math.abs

class SwipeGestureListener(
    private val context: Context,
    private val screenUtilsWrapper: ScreenUtilsWrapper,
    private val onSwipeUp: () -> Unit
) :
    GestureDetector.SimpleOnGestureListener() {
    private val swipeThreshold = 100  // Min distance to be considered a swipe
    private val swipeVelocityThreshold = 100  // Min velocity to be considered a swipe

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        LogUtils.log("SwipeGestureListener", "onFling")
        if (e1 == null) return false
        LogUtils.log("Event X:", e1.x.toString())
        LogUtils.log("Event Y:", e1.y.toString())
        LogUtils.log("Event ScreenX:", screenUtilsWrapper.getScreenWidth().toString())
        LogUtils.log("Event ScreenY:", screenUtilsWrapper.getScreenHeight().toString())
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        val screenHeight = screenUtilsWrapper.getScreenHeight()
        val screenWidth = screenUtilsWrapper.getScreenWidth()
        val isSwipeUp = (screenHeight - e1.y)/screenHeight*100f.toInt() < 50
        val isSwipeLeft = (screenWidth - e1.x)/screenWidth*100f.toInt() < 5 && isSwipeUp
        val isSwipeRight = e1.x/screenWidth*100f.toInt() < 5 && isSwipeUp

        return when {
            // Swipe Right (Back)
            diffX > swipeThreshold && abs(velocityX) > swipeVelocityThreshold && isSwipeRight -> {
                (context as? AppCompatActivity)?.onBackPressedDispatcher?.onBackPressed()
                LogUtils.log("SwipeGestureListener", "Swipe Right")
                true
            }

            // Swipe Left (Back)
            diffX < -swipeThreshold && abs(velocityX) > swipeVelocityThreshold  && isSwipeLeft -> {
                (context as? AppCompatActivity)?.onBackPressedDispatcher?.onBackPressed()
                LogUtils.log("SwipeGestureListener", "Swipe Left")
                true
            }

            // Swipe Up (Home)
            diffY < -swipeThreshold && abs(velocityY) > swipeVelocityThreshold && isSwipeUp -> {
                onSwipeUp.invoke()
                true
            }

            else -> false
        }
    }
}
