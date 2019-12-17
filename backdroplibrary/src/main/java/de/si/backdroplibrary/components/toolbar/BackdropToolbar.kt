package de.si.backdroplibrary.components.toolbar

import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.kotlinx.fade
import de.si.kotlinx.fadeIn
import de.si.kotlinx.fadeOut
import kotlinx.android.synthetic.main.layout_toolbar.*

internal class BackdropToolbar(override val backdropActivity: BackdropActivity) : BackdropComponent {

    //-----------------------------------------
    // region view elements
    //-----------------------------------------
    private val toolbarLayout: ViewGroup = backdropActivity.layout_backdrop_toolbar
    private val buttonMenu: ImageButton = backdropActivity.button_backdrop_toolbar
    private val buttonPrimaryAction: ImageButton = backdropActivity.button_backdrop_toolbar_action
    private val buttonMoreAction: ImageButton = backdropActivity.button_backdrop_toolbar_more

    private val titleLayout: LinearLayout = backdropActivity.layout_backdrop_toolbar_titles
    private val textTitle: TextView = backdropActivity.text_backdrop_toolbar_title
    private val textSubTitle: TextView = backdropActivity.text_backdrop_toolbar_subtitle

    //-----------------------------------------
    //
    //-----------------------------------------
    private var title: String
        get() = textTitle.text.toString()
        set(value) {
            textTitle.isVisible = value.isBlank().not()
            textTitle.text = value
        }

    private var subTitle: String?
        get() = textSubTitle.text.toString()
        set(value) {
            textSubTitle.isVisible = value.isNullOrBlank().not()
            textSubTitle.text = value
        }

    var currentToolbarItem: BackdropToolbarItem = BackdropToolbarItem(title = "Toolbar")
        private set
    var actionModeToolbarItem: BackdropToolbarItem? = null
        private set

    //-----------------------------------------
    //
    //-----------------------------------------
    init {
        buttonMoreAction.setOnClickListener {
            if (actionModeToolbarItem != null) {
                backdropViewModel.emit(Event.MORE_ACTION_ACTIONMODE_TRIGGERED)
            } else {
                backdropViewModel.emit(Event.MORE_ACTION_TRIGGERED)
            }
        }

        buttonPrimaryAction.setOnClickListener {
            if (actionModeToolbarItem != null) {
                backdropViewModel.emit(Event.PRIMARY_ACTION_ACTIONMODE_TRIGGERED)
            } else {
                backdropViewModel.emit(Event.PRIMARY_ACTION_TRIGGERED)
            }
        }

        buttonMenu.setOnClickListener {
            when (buttonMenu.tag as? BackdropToolbarMainButtonState) {
                BackdropToolbarMainButtonState.MENU  -> {
                    openMenuClickCallback?.invoke()
                    buttonMenu.tag = BackdropToolbarMainButtonState.CLOSE
                    buttonMenu.fade {
                        buttonMenu.setImageResource(R.drawable.ic_clear)
                    }
                }
                BackdropToolbarMainButtonState.BACK  -> {
                    backdropActivity.onBackPressed()
                }
                BackdropToolbarMainButtonState.CLOSE -> {
                    if (actionModeToolbarItem != null) {
                        finishActionModeCallback?.invoke() ?: finishActionMode(BackdropToolbarMainButtonState.MENU)
                    } else {
                        closeBackdropClickCallback?.invoke()
                        buttonMenu.tag = BackdropToolbarMainButtonState.MENU
                        buttonMenu.fade {
                            buttonMenu.setImageResource(R.drawable.ic_menu)
                        }
                    }
                }
                else                                 -> {
                    // Nothing to do here
                }
            }
        }

        buttonMenu.setOnLongClickListener {
            when (buttonMenu.tag as? BackdropToolbarMainButtonState) {
                BackdropToolbarMainButtonState.MENU -> openMenuLongClickCallback?.invoke() ?: false
                else                                -> false
            }
        }
    }

    //-----------------------------------------
    // Callbacks
    //-----------------------------------------

    internal var openMenuClickCallback: (() -> Unit)? = null
    internal var closeBackdropClickCallback: (() -> Unit)? = null
    internal var openMenuLongClickCallback: (() -> Boolean)? = null
    internal var finishActionModeCallback: (() -> Unit)? = null

    //-----------------------------------------
    // API
    //-----------------------------------------

    internal val menuButtonVisible: Boolean
        get() = buttonMenu.tag == BackdropToolbarMainButtonState.MENU

    internal val isInActionMode: Boolean
        get() = actionModeToolbarItem != null

    internal fun configure(toolbarItem: BackdropToolbarItem?, mainButtonState: BackdropToolbarMainButtonState) {
        val newToolbarItem = toolbarItem ?: currentToolbarItem

        calculateAndStartToolbarAnimations(newToolbarItem, mainButtonState)
        currentToolbarItem = newToolbarItem
    }

    private fun calculateAndStartToolbarAnimations(toolbarItem: BackdropToolbarItem, mainButtonState: BackdropToolbarMainButtonState) {
        val itemDiff: BackdropToolbarItemDiff = toolbarItem.calculateDiff(BackdropToolbarItem(title = title,
                                                                                              subtitle = subTitle,
                                                                                              primaryAction = buttonPrimaryAction.tag as Int?,
                                                                                              moreActionEnabled = buttonMoreAction.isVisible))

        val toolbarAnimations: MutableList<ObjectAnimator> = mutableListOf()

        if (itemDiff.titleChanged && itemDiff.subtitleChanged) {
            toolbarAnimations.add(ObjectAnimator.ofFloat(titleLayout, View.ALPHA, 1f, 0f))
        } else if (itemDiff.subtitleChanged) {
            titleLayout.layoutTransition = LayoutTransition()
            subTitle = toolbarItem.subtitle
            titleLayout.layoutTransition = null
        } else if (itemDiff.titleChanged) {
            toolbarAnimations.add(ObjectAnimator.ofFloat(textTitle, View.ALPHA, 1f, 0f))
        }

        if (itemDiff.moreActionChanged) {
            toolbarAnimations.add(ObjectAnimator.ofFloat(buttonMoreAction, View.ALPHA, 1f, 0f))
        }

        if (itemDiff.primaryActionChanged || (itemDiff.moreActionChanged && buttonPrimaryAction.isVisible)) {
            toolbarAnimations.add(ObjectAnimator.ofFloat(buttonPrimaryAction, View.ALPHA, 1f, 0f))
        }

        if (buttonMenu.tag != mainButtonState) {
            toolbarAnimations.add(ObjectAnimator.ofFloat(buttonMenu, View.ALPHA, 1f, 0f))
        }

        if (toolbarAnimations.isEmpty()) {
            return
        }

        val animatorSet = AnimatorSet()
        animatorSet.duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION
        animatorSet.playTogether(*toolbarAnimations.toTypedArray())
        animatorSet.addListener(onEnd = {
            title = toolbarItem.title
            subTitle = toolbarItem.subtitle
            buttonMoreAction.isVisible = toolbarItem.moreActionEnabled
            buttonPrimaryAction.tag = toolbarItem.primaryAction
            buttonPrimaryAction.isVisible = toolbarItem.primaryAction != null
            toolbarItem.primaryAction?.let { buttonPrimaryAction.setImageResource(it) }

            when (mainButtonState) {
                BackdropToolbarMainButtonState.MENU  -> {
                    buttonMenu.isVisible = true
                    buttonMenu.setImageResource(R.drawable.ic_menu)
                }
                BackdropToolbarMainButtonState.BACK  -> {
                    buttonMenu.isVisible = true
                    buttonMenu.setImageResource(R.drawable.ic_back)
                }
                BackdropToolbarMainButtonState.CLOSE -> {
                    buttonMenu.isVisible = true
                    buttonMenu.setImageResource(R.drawable.ic_clear)
                }
                BackdropToolbarMainButtonState.NONE  -> {
                    buttonMenu.isVisible = false
                }
            }

            buttonMenu.tag = mainButtonState

            animatorSet.removeAllListeners()

            toolbarAnimations.forEach { animator ->
                animator.setFloatValues(0f, 1f)
            }

            animatorSet.playTogether(*toolbarAnimations.toTypedArray())
            animatorSet.start()
        })
        animatorSet.start()
    }

    internal fun disableActions() {
        buttonPrimaryAction.fadeOut(0.5f, Backdrop.BACKDROP_ANIMATION_DURATION) {
            buttonPrimaryAction.isClickable = false
        }
        buttonMoreAction.fadeOut(0.5f, Backdrop.BACKDROP_ANIMATION_DURATION) {
            buttonMoreAction.isClickable = false
        }
    }

    internal fun enableActions() {
        buttonPrimaryAction.fadeIn(Backdrop.BACKDROP_ANIMATION_DURATION) {
            buttonPrimaryAction.isClickable = true
        }
        buttonMoreAction.fadeIn(Backdrop.BACKDROP_ANIMATION_DURATION) {
            buttonMoreAction.isClickable = true
        }
    }

    internal fun showMenuButton() {
        if (buttonMenu.tag == BackdropToolbarMainButtonState.MENU) {
            return
        }

        buttonMenu.fade {
            buttonMenu.tag = BackdropToolbarMainButtonState.MENU
            buttonMenu.isVisible = true
            buttonMenu.setImageResource(R.drawable.ic_menu)
        }
    }

    internal fun showBackdropCloseButton() {
        if (buttonMenu.tag == BackdropToolbarMainButtonState.CLOSE) {
            return
        }

        buttonMenu.fade {
            buttonMenu.tag = BackdropToolbarMainButtonState.CLOSE
            buttonMenu.isVisible = true
            buttonMenu.setImageResource(R.drawable.ic_clear)
        }
    }

    internal fun startActionMode(toolbarItem: BackdropToolbarItem) {
        calculateAndStartToolbarAnimations(toolbarItem, BackdropToolbarMainButtonState.CLOSE)
        actionModeToolbarItem = toolbarItem
    }

    internal fun finishActionMode(mainButtonState: BackdropToolbarMainButtonState) {
        calculateAndStartToolbarAnimations(currentToolbarItem, mainButtonState)
        actionModeToolbarItem = null
        backdropViewModel.emit(Event.ACTION_MODE_FINISHED)
    }
}