package de.si.kotlinx

import android.animation.ObjectAnimator
import android.view.View

val View.fadeInAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)

val View.fadeOutAnimator: ObjectAnimator
    get() = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)