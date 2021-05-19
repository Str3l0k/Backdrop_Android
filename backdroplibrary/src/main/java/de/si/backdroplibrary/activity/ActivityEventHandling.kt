package de.si.backdroplibrary.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.animation.doOnStart
import androidx.core.graphics.luminance
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem
import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

internal fun BackdropActivity.onEvent(event: BackdropEvent): Boolean {
    return when (event) {
        // content events
        is BackdropEvent.PrefetchBackdropContentView     -> handlePrefetchBackdropContentEvent(event.layoutRes)
        is BackdropEvent.ShowBackdropContentView         -> handleShowBackdropContentEvent(event.layoutRes)
        BackdropEvent.HideBackdropContentView            -> handleHideBackdropContentEvent()
        is BackdropEvent.BackdropContentNowVisible       -> onBackdropContentVisible(event.view)
        BackdropEvent.BackdropContentNowHidden           -> onBackdropContentInvisible()
        is BackdropEvent.ChangeBackdropColor             -> fadeBackgroundColor(event.color)

        // toolbar events
        BackdropEvent.PrimaryActionTriggered             -> onPrimaryActionClicked()
        BackdropEvent.MoreActionTriggered                -> onMoreActionClicked()
        is BackdropEvent.ChangeToolbarItem               -> handleToolbarItemChangedEvent(event.toolbarItem)
        is BackdropEvent.StartActionMode                 -> handleToolbarActionModeStart(event.actionModeToolbarItem)
        BackdropEvent.FinishActionMode                   -> handleToolbarActionModeFinish()
        BackdropEvent.PrimaryActionInActionModeTriggered -> onPrimaryActionInActionModeClicked()
        BackdropEvent.MoreActionInActionModeTriggered    -> onMoreActionInActionModeClicked()
        BackdropEvent.ActionModeFinished                 -> onToolbarActionModeFinished()
        BackdropEvent.MenuActionTriggered                -> onMenuActionClicked()

        // card stack events
        is BackdropEvent.AddTopCard                      -> handleAddTopCardEvent(event.fragment)
        BackdropEvent.RemoveTopCard                      -> handleRemoveTopCardEvent()

        // fullscreen
        is BackdropEvent.ShowFullscreenFragment          -> handleShowFullscreenFragmentEvent(event.fragment)
        is BackdropEvent.RevealFullscreenFragment        -> handleRevealFullscreenFragmentEvent(event.fragment)
        BackdropEvent.HideFullscreenFragment             -> handleHideFullscreenFragmentEvent()
    }
}

//-----------------------------------------
// Backdrop content event handling
//-----------------------------------------

private fun BackdropActivity.handlePrefetchBackdropContentEvent(layoutResId: Int): Boolean {
    backdropContent.preCacheContentView(layoutResId)
    return true
}

private fun BackdropActivity.handleShowBackdropContentEvent(layoutResId: Int): Boolean {
    backdropContent.showContentView(layoutResId) { contentView ->
        backdropToolbar.disableActions()
        backdropToolbar.showCloseButton()
        backdropCardStack.disable()
        animateBackdropOpening(contentView.height.toFloat())
    }
    return true
}

private fun BackdropActivity.handleHideBackdropContentEvent(): Boolean {
    animateBackdropClosing()
    backdropContent.hide()
    backdropCardStack.enable()
    backdropToolbar.enableActions()

    when {
        backdropToolbar.isInActionMode        -> backdropToolbar.showCloseButton()
        backdropCardStack.hasMoreThanOneEntry -> backdropToolbar.showBackButton()
        else                                  -> backdropToolbar.showMenuButton()
    }
    backdropViewModel.emit(BackdropEvent.BackdropContentNowHidden)
    return true
}

//-----------------------------------------
// toolbar event handling
//-----------------------------------------

private fun BackdropActivity.handleToolbarItemChangedEvent(toolbarItem: BackdropToolbarItem): Boolean {
    val mainButtonState: BackdropToolbarMainButtonState = if (backdropCardStack.hasMoreThanOneEntry) {
        BackdropToolbarMainButtonState.BACK
    } else {
        baseCardFragment.menuButtonState
    }

    backdropToolbar.configure(toolbarItem, mainButtonState)
    return true
}

private fun BackdropActivity.handleToolbarActionModeStart(toolbarItem: BackdropToolbarItem): Boolean {
    backdropToolbar.startActionMode(toolbarItem)
    return true
}

private fun BackdropActivity.handleToolbarActionModeFinish(): Boolean {
    val menuRes: Int? = baseCardFragment.mainMenuRes

    // TODO extract
    val mainButtonState: BackdropToolbarMainButtonState = when {
        backdropCardStack.hasMoreThanOneEntry -> BackdropToolbarMainButtonState.BACK
        menuRes == null                       -> BackdropToolbarMainButtonState.NO_LAYOUT_ERROR
        else                                  -> baseCardFragment.menuButtonState
    }

    backdropToolbar.finishActionMode(mainButtonState)
    return true
}

private fun BackdropActivity.fadeBackgroundColor(color: Int): Boolean {
    val colorDrawable = layout_backdrop.background as? ColorDrawable ?: return true

    val newTextColor = when {
        color.luminance < 0.45 -> Color.WHITE
        else                   -> Color.BLACK
    }

    val backgroundColorAnimator = ObjectAnimator.ofArgb(layout_backdrop, "backgroundColor", colorDrawable.color, color)
    val textColorAnimator = ObjectAnimator.ofArgb(
        layout_backdrop_toolbar_titles.text_backdrop_toolbar_title.currentTextColor,
        newTextColor
    )

    textColorAnimator.addUpdateListener { animator ->
        val colorValue: Int = animator.animatedValue as Int
        backdropToolbar.setTintColor(colorValue)
    }

    AnimatorSet().apply {
        playTogether(backgroundColorAnimator, textColorAnimator)
        doOnStart { configureStatusBarAppearance(color) }
    }.start()

    return true
}

//-----------------------------------------
// card stack event handling
//-----------------------------------------

private fun BackdropActivity.handleAddTopCardEvent(cardFragment: CardBackdropFragment): Boolean {
    backdropCardStack.push(cardFragment)
    backdropToolbar.configure(cardFragment.toolbarItem, BackdropToolbarMainButtonState.BACK)

    return true
}

private fun BackdropActivity.handleRemoveTopCardEvent(): Boolean {
    backdropCardStack.pop()

    val menuRes: Int? = baseCardFragment.mainMenuRes

    val mainButtonState: BackdropToolbarMainButtonState = when {
        backdropCardStack.hasMoreThanOneEntry -> BackdropToolbarMainButtonState.BACK
        menuRes == null                       -> BackdropToolbarMainButtonState.NO_LAYOUT_ERROR
        else                                  -> baseCardFragment.menuButtonState
    }

    backdropToolbar.configure(backdropCardStack.topFragment.toolbarItem, mainButtonState)

    return true
}

//-----------------------------------------
// fullscreen fragment event handling
//-----------------------------------------
private fun BackdropActivity.handleShowFullscreenFragmentEvent(fragment: FullscreenBackdropFragment): Boolean {
    fullscreenDialogs.showFullscreenFragment(fragment)

    return true
}

private fun BackdropActivity.handleHideFullscreenFragmentEvent(): Boolean {
    fullscreenDialogs.hideFullscreenFragment()

    return true
}

private fun BackdropActivity.handleRevealFullscreenFragmentEvent(fragment: FullscreenRevealBackdropFragment): Boolean {
    fullscreenDialogs.revealFullscreen(fragment)

    return true
}
