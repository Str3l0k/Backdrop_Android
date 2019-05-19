package de.si.backdroplibrary.components

import android.animation.AnimatorSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.kotlinx.*
import kotlinx.android.synthetic.main.backdrop_base.*

class BackdropToolbar(activity: BackdropActivity) : BackdropComponent(activity) {

    /* view elements */
    private val buttonCloseBackdrop: ImageButton = activity.button_backdrop_toolbar_hide
    private val buttonOpenMenu: ImageButton = activity.button_backdrop_toolbar_menu_show
    private val buttonPrimaryAction: ImageButton = activity.button_backdrop_toolbar_action
    private val buttonMoreAction: ImageButton = activity.button_backdrop_toolbar_more

    private val textTitle: TextView = activity.text_backdrop_toolbar_title
    private val textSubTitle: TextView = activity.text_backdrop_toolbar_subtitle

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
        duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION

        doOnEnd {
            buttonCloseBackdrop.alpha = 0f
            buttonCloseBackdrop.isVisible = false
        }
    }

    private val showCloseButtonAnimatorSet = AnimatorSet().apply {
        duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION
        playSequentially(buttonMenuHideAnimator, buttonCloseShowAnimator)
    }
    private val showMenuButtonAnimatorSet = AnimatorSet().apply {
        duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION
        playSequentially(buttonCloseHideAnimator, buttonMenuShowAnimator)
    }

    // additional constructor init
    init {
        buttonMoreAction.setOnClickListener {
            viewModel.emit(BackdropEvent.MORE_ACTION_TRIGGERED)
        }

        buttonPrimaryAction.setOnClickListener {
            viewModel.emit(BackdropEvent.PRIMARY_ACTION_TRIGGERED)
        }
    }

    /* callbacks */
    internal var openMenuClickCallback: (() -> Unit)? = null
        set(value) {
            field = value
            buttonOpenMenu.isVisible = value != null
            buttonOpenMenu.setOnClickListener { value?.invoke() }
        }

    internal var closeBackdropClickCallback: (() -> Unit)? = null
        set(value) {
            field = value
            buttonCloseBackdrop.setOnClickListener { value?.invoke() }
        }

    internal var openMenuLongClickCallback: (() -> Boolean)? = null
        set(value) {
            field = value
            buttonOpenMenu.setOnLongClickListener { value?.invoke() ?: false }
        }

    /* API */
    internal var title: String?
        get() = textTitle.text.toString()
        set(value) {
            textTitle.isVisible = value.isNullOrBlank().not()
            textTitle.fadeTextChange(value)
        }

    internal var subTitle: String?
        get() = textSubTitle.text.toString()
        set(value) {
            textSubTitle.isVisible = value.isNullOrBlank().not()
            textSubTitle.fadeTextChange(value)
        }

    internal fun disableActions() { // TODO refactor
        buttonPrimaryAction.isClickable = false
        buttonPrimaryAction.animate().alpha(0.5f).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION).start()

        buttonMoreAction.isClickable = false
        buttonMoreAction.animate().alpha(0.5f).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION).start()
    }

    internal fun enableActions() { // TODO refactor
        buttonPrimaryAction.isClickable = true
        buttonPrimaryAction.animate().alpha(1f).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION).start()

        buttonMoreAction.isClickable = true
        buttonMoreAction.animate().alpha(1f).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION).start()
    }

    internal fun activatePrimaryAction(@DrawableRes drawableResId: Int) {
        buttonPrimaryAction.isVisible = true
        buttonPrimaryAction.fadeIn()
        buttonPrimaryAction.setImageResource(drawableResId)
    }

    internal fun deactivatePrimaryAction() {
        buttonPrimaryAction.isVisible = false
        buttonPrimaryAction.setOnClickListener(null)
    }

    internal fun activateMoreAction() {
        buttonMoreAction.isVisible = true
        buttonMoreAction.fadeIn()
    }

    internal fun deactivateMoreAction() {
        buttonMoreAction.fadeOut {
            buttonMoreAction.isVisible = false
        }
    }

    internal fun showMenuButton() {
        buttonOpenMenu.isVisible = true
        showMenuButtonAnimatorSet.start()
    }

    internal fun showBackdropCloseButton() {
        buttonCloseBackdrop.isVisible = true
        showCloseButtonAnimatorSet.start()
    }
}