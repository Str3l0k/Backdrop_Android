package de.si.backdroplibrary.components.toolbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.kotlinx.fade
import de.si.kotlinx.fadeIn
import de.si.kotlinx.fadeInAnimator
import de.si.kotlinx.fadeOut
import de.si.kotlinx.fadeOutAnimator
import kotlinx.android.synthetic.main.layout_toolbar.*

internal class BackdropToolbar(override val backdropActivity: BackdropActivity) : BackdropComponent {

    //-----------------------------------------
    // View elements
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
    var currentToolbarItem: BackdropToolbarItem = BackdropToolbarItem(title = "Toolbar")
        private set
    var actionModeToolbarItem: BackdropToolbarItem? = null
        private set

    internal val menuButtonVisible: Boolean
        get() = buttonMenu.tag == BackdropToolbarMainButtonState.MENU

    internal val isInActionMode: Boolean
        get() = actionModeToolbarItem != null

    //-----------------------------------------
    //
    //-----------------------------------------
    init {
        configureMenuClickListener()
        configureActionClickListeners()
    }

    private fun configureMenuClickListener() {
        buttonMenu.setOnClickListener {
            when (buttonMenu.tag as? BackdropToolbarMainButtonState) {
                BackdropToolbarMainButtonState.MENU  -> {
                    backdropViewModel.emit(Event.MENU_ACTION_TRIGGERED)
                    showCloseButton()
                }
                BackdropToolbarMainButtonState.BACK,
                BackdropToolbarMainButtonState.CLOSE -> backdropActivity.onBackPressed()
                else                                 -> Unit
            }
        }
    }

    private fun configureActionClickListeners() {
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
    }

    //-----------------------------------------
    // API
    //-----------------------------------------
    internal fun configure(
        toolbarItem: BackdropToolbarItem?,
        mainButtonState: BackdropToolbarMainButtonState
    ) {

        val newToolbarItem = toolbarItem ?: currentToolbarItem
        calculateAndStartToolbarAnimations(
                oldToolbarItem = currentToolbarItem,
                newToolbarItem = newToolbarItem,
                mainButtonState = mainButtonState
        ) {
            currentToolbarItem = newToolbarItem
        }
    }

    internal fun disableActions() {
        buttonPrimaryAction.fadeOut(alpha = 0.5f, duration = Backdrop.BACKDROP_ANIMATION_DURATION) {
            buttonPrimaryAction.isClickable = false
        }
        buttonMoreAction.fadeOut(alpha = 0.5f, duration = Backdrop.BACKDROP_ANIMATION_DURATION) {
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

    internal fun showCloseButton() {
        if (buttonMenu.tag == BackdropToolbarMainButtonState.CLOSE) {
            return
        }

        buttonMenu.fade {
            buttonMenu.tag = BackdropToolbarMainButtonState.CLOSE
            buttonMenu.isVisible = true
            buttonMenu.setImageResource(R.drawable.ic_clear)
        }
    }

    internal fun showBackButton() {
        if (buttonMenu.tag == BackdropToolbarMainButtonState.BACK) {
            return
        }

        buttonMenu.fade {
            buttonMenu.tag = BackdropToolbarMainButtonState.BACK
            buttonMenu.isVisible = true
            buttonMenu.setImageResource(R.drawable.ic_back)
        }
    }

    internal fun startActionMode(toolbarItem: BackdropToolbarItem) {
        calculateAndStartToolbarAnimations(
                oldToolbarItem = currentToolbarItem,
                newToolbarItem = toolbarItem,
                mainButtonState = BackdropToolbarMainButtonState.CLOSE
        ) {
            actionModeToolbarItem = toolbarItem
        }
    }

    internal fun finishActionMode(mainButtonState: BackdropToolbarMainButtonState) {
        if(actionModeToolbarItem == null) {
            Log.w("Backdrop", "Trying to finish action mode without starting it.")
            return
        }

        calculateAndStartToolbarAnimations(
                oldToolbarItem = requireNotNull(actionModeToolbarItem),
                newToolbarItem = currentToolbarItem,
                mainButtonState = mainButtonState
        ) {
            actionModeToolbarItem = null
            backdropViewModel.emit(Event.ACTION_MODE_FINISHED)
        }
    }

    //-----------------------------------------
    // Helper
    //-----------------------------------------

    private fun calculateAndStartToolbarAnimations(
        oldToolbarItem: BackdropToolbarItem,
        newToolbarItem: BackdropToolbarItem,
        mainButtonState: BackdropToolbarMainButtonState,
        changeContentBlock: () -> Unit
    ) {
        val itemDiff: BackdropToolbarItemDiff = newToolbarItem.calculateDiff(oldToolbarItem)

        val hideAnimations: List<Animator> = calculateChangeHideAnimations(itemDiff, mainButtonState)
        val reappearAnimations: List<Animator> = calculateChangeReappearAnimations(itemDiff, mainButtonState)

        val reappearAnimatorSet = AnimatorSet().apply {
            playTogether(reappearAnimations)
            duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION
        }

        val hideAnimatorSet = AnimatorSet().apply {
            playTogether(hideAnimations)
            duration = Backdrop.BACKDROP_ANIMATION_HALF_DURATION

            doOnEnd {
                updateContent(newToolbarItem, mainButtonState)
                changeContentBlock()
                reappearAnimatorSet.start()
            }
        }

        hideAnimatorSet.start()
    }

    private fun calculateChangeReappearAnimations(
        toolbarItemDiff: BackdropToolbarItemDiff,
        mainButtonState: BackdropToolbarMainButtonState
    ): List<Animator> {

        val toolbarAnimations: MutableList<ObjectAnimator> = mutableListOf()

        if (toolbarItemDiff.titleChanged || toolbarItemDiff.subtitleChanged) {
            toolbarAnimations.add(titleLayout.fadeInAnimator)
        }

        if (toolbarItemDiff.moreActionChanged) {
            toolbarAnimations.add(buttonMoreAction.fadeInAnimator)
        }

        if (toolbarItemDiff.primaryActionChanged || (toolbarItemDiff.moreActionChanged && buttonPrimaryAction.isVisible)) {
            toolbarAnimations.add(buttonPrimaryAction.fadeInAnimator)
        }

        if (buttonMenu.tag != mainButtonState) {
            toolbarAnimations.add(buttonMenu.fadeInAnimator)
        }

        return toolbarAnimations
    }

    private fun calculateChangeHideAnimations(
        toolbarItemDiff: BackdropToolbarItemDiff,
        mainButtonState: BackdropToolbarMainButtonState
    ): List<Animator> {

        val toolbarAnimations: MutableList<ObjectAnimator> = mutableListOf()

        if (toolbarItemDiff.titleChanged || toolbarItemDiff.subtitleChanged) {
            toolbarAnimations.add(titleLayout.fadeOutAnimator)
        }

        if (toolbarItemDiff.moreActionChanged) {
            toolbarAnimations.add(buttonMoreAction.fadeOutAnimator)
        }

        if (toolbarItemDiff.primaryActionChanged || (toolbarItemDiff.moreActionChanged && buttonPrimaryAction.isVisible)) {
            toolbarAnimations.add(buttonPrimaryAction.fadeOutAnimator)
        }

        if (mainButtonState != buttonMenu.tag) {
            toolbarAnimations.add(buttonMenu.fadeOutAnimator)
        }

        return toolbarAnimations
    }

    private fun updateContent(
        newToolbarItem: BackdropToolbarItem,
        mainButtonState: BackdropToolbarMainButtonState
    ) {

        textTitle.text = newToolbarItem.title
        textTitle.visibility = if (newToolbarItem.hasTitle()) View.VISIBLE else View.GONE

        textSubTitle.text = newToolbarItem.subtitle
        textSubTitle.visibility = if (newToolbarItem.hasSubtitle()) View.VISIBLE else View.GONE

        buttonMoreAction.isVisible = newToolbarItem.moreActionEnabled

        buttonPrimaryAction.tag = newToolbarItem.primaryAction
        buttonPrimaryAction.isVisible = newToolbarItem.hasPrimaryAction()
        buttonPrimaryAction.setImageResource(newToolbarItem.primaryAction ?: 0)

        val (shouldBeVisible: Boolean, imageResId: Int) = when (mainButtonState) {
            BackdropToolbarMainButtonState.MENU  -> true to R.drawable.ic_menu
            BackdropToolbarMainButtonState.BACK  -> true to R.drawable.ic_back
            BackdropToolbarMainButtonState.CLOSE -> true to R.drawable.ic_clear
            BackdropToolbarMainButtonState.NONE  -> false to -1
        }

        buttonMenu.apply {
            isVisible = shouldBeVisible
            setImageResource(imageResId)
            tag = mainButtonState
        }
    }
}
