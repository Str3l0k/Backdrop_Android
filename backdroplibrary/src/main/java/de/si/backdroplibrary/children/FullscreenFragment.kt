package de.si.backdroplibrary.children

import android.transition.Slide
import android.view.Gravity
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.activity.Activity

abstract class FullscreenFragment : Fragment() {
    override val activity: Activity
        get() = getActivity() as Activity

    init {
        enterTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
        exitTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
    }
}