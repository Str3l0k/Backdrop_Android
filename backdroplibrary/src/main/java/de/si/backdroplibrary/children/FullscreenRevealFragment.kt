package de.si.backdroplibrary.children

import android.graphics.Point
import android.transition.Fade
import de.si.backdroplibrary.activity.Activity

abstract class FullscreenRevealFragment : Fragment() {
    override val activity: Activity
        get() = getActivity() as Activity

    var revealEpiCenter: Point = Point()

    init {
        enterTransition = Fade()
        exitTransition = Fade()
    }
}