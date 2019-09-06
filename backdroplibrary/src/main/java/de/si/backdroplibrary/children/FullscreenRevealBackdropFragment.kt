package de.si.backdroplibrary.children

import android.graphics.Point
import android.transition.Fade
import de.si.backdroplibrary.activity.BackdropActivity

abstract class FullscreenRevealBackdropFragment : BackdropFragment() {
    override val backdropActivity: BackdropActivity
        get() = activity as BackdropActivity

    var revealEpiCenter: Point = Point()

    init {
        enterTransition = Fade()
        exitTransition = Fade()
    }
}