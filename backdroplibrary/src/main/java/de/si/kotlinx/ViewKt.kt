package de.si.kotlinx

import android.animation.ObjectAnimator
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams

val View.revealRadius
    get() = Math.hypot(width.toDouble(), height.toDouble()).toFloat()

val View.globalVisibleRect: Rect
    get() {
        return Rect().apply {
            getGlobalVisibleRect(this)
        }
    }

val View.globalCenterPoint: Point
    get() {
        val rect = globalVisibleRect
        return Point(rect.centerX(), rect.centerY())
    }

val View.fadeInAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)

val View.fadeOutAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)

fun View.fadeOut(alpha: Float = 0f, duration: Long = 150, fadedOutCallback: (() -> Unit)? = null) {
    animate()
        .alpha(alpha)
        .setDuration(duration)
        .withEndAction(fadedOutCallback)
}

fun View.fadeIn(duration: Long = 150, fadedInCallback: (() -> Unit)? = null) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .withEndAction(fadedInCallback)
}

fun View.fade(fadeCallback: (() -> Unit)?) {
    fadeOut {
        fadeCallback?.invoke()
        fadeIn()
    }
}

fun View.setTopMargin(margin: Int) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = margin
    }
}
