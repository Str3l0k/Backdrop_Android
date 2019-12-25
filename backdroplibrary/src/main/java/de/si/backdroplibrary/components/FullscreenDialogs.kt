package de.si.backdroplibrary.components

import android.graphics.Color
import android.transition.Transition
import android.view.ViewAnimationUtils
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.transition.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.R
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.kotlinx.add
import de.si.kotlinx.remove
import de.si.kotlinx.revealRadius
import kotlinx.android.synthetic.main.layout_main.*

internal class FullscreenDialogs(override val backdropActivity: BackdropActivity) : BackdropComponent {

    private val fragmentManager = backdropActivity.supportFragmentManager
    private val layoutContainer = backdropActivity.layout_backdrop_overlay

    val isVisible
        get() = layoutContainer.isVisible

    override fun showFullscreenFragment(fragment: FullscreenBackdropFragment) {
        layoutContainer.setBackgroundColor(Color.TRANSPARENT)
        layoutContainer.isVisible = true
        fragmentManager.add(fragment, layoutContainer.id)
    }

    override fun hideFullscreenFragment() {
        when (val fragment = fragmentManager.findFragmentById(layoutContainer.id)) {
            is FullscreenBackdropFragment -> hideFullScreen(fragment)
            is FullscreenRevealBackdropFragment -> concealFullscreen(fragment)
        }
    }

    internal fun revealFullscreen(fragment: FullscreenRevealBackdropFragment) {
        val epiCenter = fragment.revealEpiCenter

        layoutContainer.setBackgroundColor(
                ContextCompat.getColor(
                        backdropActivity.applicationContext,
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

    private fun hideFullScreen(fragment: FullscreenBackdropFragment) {
        val transition = fragment.exitTransition as? Transition
        transition?.doOnEnd {
            layoutContainer.isVisible = false
        }
        fragmentManager.remove(fragment)
    }

    private fun concealFullscreen(fragment: FullscreenRevealBackdropFragment) {
        val epiCenter = fragment.concealEpiCenter

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
