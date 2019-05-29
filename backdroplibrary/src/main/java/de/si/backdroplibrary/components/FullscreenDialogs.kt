package de.si.backdroplibrary.components

import android.graphics.Color
import android.transition.Transition
import android.view.ViewAnimationUtils
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.transition.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.Component
import de.si.backdroplibrary.R
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.FullscreenFragment
import de.si.backdroplibrary.children.FullscreenRevealFragment
import de.si.kotlinx.add
import de.si.kotlinx.remove
import de.si.kotlinx.revealRadius
import kotlinx.android.synthetic.main.layout_main.*

internal class FullscreenDialogs(override val activity: Activity) : Component {
    private val fragmentManager = activity.supportFragmentManager
    private val layoutContainer = activity.layout_backdrop_overlay

    val isVisible
        get() = layoutContainer.isVisible

    override fun showFullscreenFragment(fragment: FullscreenFragment) {
        layoutContainer.setBackgroundColor(Color.TRANSPARENT)
        layoutContainer.isVisible = true
        fragmentManager.add(fragment, layoutContainer.id)
    }

    override fun hideFullscreenFragment() {
        when (val fragment = fragmentManager.findFragmentById(layoutContainer.id)) {
            is FullscreenFragment -> hideFullScreen(fragment)
            is FullscreenRevealFragment -> concealFullscreen(fragment)
        }
    }

    internal fun revealFullscreen(fragment: FullscreenRevealFragment) {
        val epiCenter = fragment.revealEpiCenter

        layoutContainer.setBackgroundColor(
            ContextCompat.getColor(
                activity.applicationContext,
                R.color.colorPrimaryLightLowAlpha
            )
        )
        layoutContainer.isVisible = true
        fragmentManager.add(fragment, layoutContainer.id)

        ViewAnimationUtils.createCircularReveal(
            layoutContainer,
            epiCenter.x,
            epiCenter.y,
            0f,
            layoutContainer.revealRadius
        ).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION).start()
    }

    private fun hideFullScreen(fragment: FullscreenFragment) {
        val transition = fragment.exitTransition as? Transition
        transition?.doOnEnd {
            layoutContainer.isVisible = false
        }
        fragmentManager.remove(fragment)
    }

    private fun concealFullscreen(fragment: FullscreenRevealFragment) {
        val epiCenter = fragment.revealEpiCenter

        val concealAnimator = ViewAnimationUtils.createCircularReveal(
            layoutContainer,
            epiCenter.x,
            epiCenter.y,
            layoutContainer.revealRadius,
            0f
        ).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
        concealAnimator.doOnEnd {
            layoutContainer.isVisible = false
        }

        concealAnimator.start()
        fragmentManager.remove(fragment)
    }
}