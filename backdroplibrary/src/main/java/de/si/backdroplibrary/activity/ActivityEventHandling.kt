package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem
import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*

internal fun BackdropActivity.onEvent(event: Event, payload: Any?): Boolean {
    return when (event) {
        // content events
        Event.PREFETCH_BACKDROP_CONTENT_VIEW -> handlePrefetchBackdropContentEvent(payload as Int)
        Event.SHOW_BACKDROP_CONTENT -> handleShowBackdropContentEvent(payload as Int)
        Event.HIDE_BACKDROP_CONTENT -> handleHideBackdropContentEvent()
        Event.BACKDROP_CONTENT_VISIBLE -> onBackdropContentVisible(payload as View)
        Event.BACKDROP_CONTENT_INVISIBLE -> onBackdropContentInvisible()

        // toolbar events
        Event.CHANGE_NAVIGATION_ITEM -> handleToolbarItemChangedEvent(payload as BackdropToolbarItem)
        Event.PRIMARY_ACTION_TRIGGERED -> onPrimaryActionClicked()
        Event.MORE_ACTION_TRIGGERED -> onMoreActionClicked()
        Event.START_ACTION_MODE -> handleToolbarActionModeStart(payload as BackdropToolbarItem)
        Event.FINISH_ACTION_MODE -> handleToolbarActionModeFinish()
        Event.PRIMARY_ACTION_ACTIONMODE_TRIGGERED -> onPrimaryActionInActionModeClicked()
        Event.MORE_ACTION_ACTIONMODE_TRIGGERED -> onMoreActionInActionModeClicked()
        Event.ACTION_MODE_FINISHED -> onToolbarActionModeFinished()
        Event.MENU_ACTION_TRIGGERED -> onMenuActionClicked()
        Event.FADE_COLOR -> fadeBackgroundColor(payload as Int)

        // card stack events
        Event.ADD_TOP_CARD -> handleAddTopCardEvent(payload as CardBackdropFragment)
        Event.REMOVE_TOP_CARD -> handleRemoveTopCardEvent()

        // fullscreen
        Event.SHOW_FULLSCREEN_FRAGMENT -> handleShowFullscreenFragmentEvent(payload as FullscreenBackdropFragment)
        Event.REVEAL_FULLSCREEN_FRAGMENT -> handleRevealFullscreenFragmentEvent(payload as FullscreenRevealBackdropFragment)
        Event.HIDE_FULLSCREEN_FRAGMENT -> handleHideFullscreenFragmentEvent()
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
    backdropViewModel.emit(Event.BACKDROP_CONTENT_INVISIBLE)
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
    val mainButtonState: BackdropToolbarMainButtonState = if (backdropCardStack.hasMoreThanOneEntry) {
        BackdropToolbarMainButtonState.BACK
    } else {
        baseCardFragment.menuButtonState
    }

    backdropToolbar.finishActionMode(mainButtonState)
    return true
}

private fun BackdropActivity.fadeBackgroundColor(color: Int): Boolean {
    val colorDrawable = layout_backdrop.background as? ColorDrawable ?: return true

    val luminance: Double = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue) / 255

    println("Luminance: $luminance")

    ObjectAnimator.ofArgb(layout_backdrop, "backgroundColor", colorDrawable.color, color).start()

    if (luminance < 0.42) {
        button_backdrop_toolbar.button_backdrop_toolbar.setColorFilter(Color.WHITE)
        button_backdrop_toolbar_action.setColorFilter(Color.WHITE)
        button_backdrop_toolbar_more.setColorFilter(Color.WHITE)
        layout_backdrop_toolbar_titles.text_backdrop_toolbar_title.setTextColor(Color.WHITE)
        layout_backdrop_toolbar_titles.text_backdrop_toolbar_subtitle.setTextColor(Color.WHITE)
    } else {
        button_backdrop_toolbar.button_backdrop_toolbar.setColorFilter(Color.BLACK)
        button_backdrop_toolbar_action.setColorFilter(Color.BLACK)
        button_backdrop_toolbar_more.setColorFilter(Color.BLACK)
        layout_backdrop_toolbar_titles.text_backdrop_toolbar_title.setTextColor(Color.BLACK)
        layout_backdrop_toolbar_titles.text_backdrop_toolbar_subtitle.setTextColor(Color.BLACK)
    }

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

    val mainButtonState: BackdropToolbarMainButtonState = if (backdropCardStack.hasMoreThanOneEntry) {
        BackdropToolbarMainButtonState.BACK
    } else {
        baseCardFragment.menuButtonState
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
