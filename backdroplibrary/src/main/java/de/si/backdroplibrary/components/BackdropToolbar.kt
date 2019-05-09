package de.si.backdroplibrary.components

import android.animation.AnimatorSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.BackdropActivity
import de.si.backdroplibrary.BackdropViewModel
import de.si.kotlinx.fadeInAnimator
import de.si.kotlinx.fadeOutAnimator
import kotlinx.android.synthetic.main.backdrop_base.*

class BackdropToolbar(activity: BackdropActivity) {
    // TODO menu button
    // TODO titles
    // TODO action items

    /* view elements */
    private val buttonCloseBackdrop: ImageButton = activity.button_backdrop_toolbar_hide
    private val buttonOpenMenu: ImageButton = activity.button_backdrop_toolbar_menu_show
    private val buttonPrimaryAction: ImageButton = activity.button_backdrop_toolbar_action
    private val buttonMoreAction: ImageButton = activity.button_backdrop_toolbar_more

    private val textTitle: TextView = activity.text_backdrop_toolbar_title
    private val textSubTitle: TextView = activity.text_backdrop_toolbar_subtitle

    /* view model */
    private val viewModel = BackdropViewModel.registeredInstance(activity)

    /* animations */
    private val buttonMenuShowAnimator = buttonOpenMenu.fadeInAnimator
    private val buttonMenuHideAnimator = buttonOpenMenu.fadeOutAnimator.apply {
        doOnEnd {
            buttonOpenMenu.alpha = 0f
            buttonOpenMenu.visibility = View.INVISIBLE
        }
    }
    private val buttonCloseShowAnimator = buttonCloseBackdrop.fadeInAnimator
    private val buttonCloseHideAnimator = buttonCloseBackdrop.fadeOutAnimator.apply {
        duration = BackdropActivity.halfBackdropAnimationDuration

        doOnEnd {
            buttonCloseBackdrop.alpha = 0f
            buttonCloseBackdrop.isVisible = false
        }
    }

    private val showCloseButtonAnimatorSet = AnimatorSet().apply {
        duration = BackdropActivity.halfBackdropAnimationDuration
        playSequentially(buttonMenuHideAnimator, buttonCloseShowAnimator)
    }
    private val showMenuButtonAnimatorSet = AnimatorSet().apply {
        duration = BackdropActivity.halfBackdropAnimationDuration
        playSequentially(buttonCloseHideAnimator, buttonMenuShowAnimator)
    }

    /* callbacks */
    internal var openMenuClickCallback: (() -> Unit)? = null
        set(value) {
            field = value
            buttonOpenMenu.setOnClickListener { value?.invoke() }
        }

    internal var closeBackdropClickCallback: (() -> Unit)? = null
        set(value) {
            field = value
            buttonCloseBackdrop.setOnClickListener { value?.invoke() }
        }

    /* API */
    var title: String?
        get() = textTitle.text.toString()
        set(value) {
            textTitle.isVisible = value.isNullOrBlank()
            textTitle.text = value
        }

    var subTitle: String?
        get() = textSubTitle.text.toString()
        set(value) {
            textSubTitle.isVisible = value.isNullOrBlank()
            textSubTitle.text = value
        }

    fun activatePrimaryAction(drawableResId: Int, callback: () -> Unit) {
        buttonPrimaryAction.isVisible = true
        buttonPrimaryAction.setImageResource(drawableResId)
        buttonPrimaryAction.setOnClickListener {
            callback()
        }
    }

    fun deactivatePrimaryAction() {
        buttonPrimaryAction.isVisible = false
        buttonPrimaryAction.setOnClickListener(null)
    }

    fun activateMoreAction() {

    }

    fun deactivateMoreAction() {

    }

    fun showMenuButton() {
        buttonOpenMenu.isVisible = true
        showMenuButtonAnimatorSet.start()
    }

    fun showBackdropCloseButton() {
        buttonCloseBackdrop.isVisible = true
        showCloseButtonAnimatorSet.start()
    }
}