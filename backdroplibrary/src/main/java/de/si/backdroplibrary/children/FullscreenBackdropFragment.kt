package de.si.backdroplibrary.children

import android.transition.Slide
import android.view.Gravity
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.activity.BackdropActivity

abstract class FullscreenBackdropFragment : BackdropFragment() {
    override val backdropActivity: BackdropActivity
        get() = activity as BackdropActivity

    init {
        enterTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
        exitTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
    }
}