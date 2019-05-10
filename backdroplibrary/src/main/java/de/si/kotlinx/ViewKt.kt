package de.si.kotlinx

import android.animation.ObjectAnimator
import android.view.View
import androidx.core.view.isVisible

val View.fadeInAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)

val View.fadeOutAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)

fun View.fadeOut(duration: Long = 150, fadedOutCallback: (() -> Unit)? = null) {
    animate()
        .alpha(0f)
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