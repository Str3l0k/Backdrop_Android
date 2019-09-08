package de.si.backdroplibrary.gestures

import android.view.GestureDetector
import android.view.MotionEvent

class BackdropGestureNavigationListener : GestureDetector.SimpleOnGestureListener() {

    //-----------------------------------------
    // Gesture callback
    //-----------------------------------------
    var onFlingDownCallback: (() -> Unit)? = null
    var onFlingUpCallback: (() -> Unit)? = null

    //-----------------------------------------
    // Listener implementation
    //-----------------------------------------
    override fun onFling(eventStart: MotionEvent?, eventEnd: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        println("BackdropGestureNavigationListener.onFling + $velocityY")

        return when {
            velocityY > 1000 -> {
                onFlingDownCallback?.invoke()
                true
            }
            velocityY <= 1000 -> {
                onFlingUpCallback?.invoke()
                true
            }
            else -> super.onFling(eventStart, eventEnd, velocityX, velocityY)
        }
    }
}