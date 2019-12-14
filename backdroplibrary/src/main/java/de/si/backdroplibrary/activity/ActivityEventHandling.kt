package de.si.backdroplibrary.activity

import android.view.View
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import de.si.backdroplibrary.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem
import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState

internal fun BackdropActivity.onEvent(event: Event, payload: Any?): Boolean {
    return when (event) {
        // content events
        Event.PREFETCH_BACKDROP_CONTENT_VIEW      -> handlePrefetchBackdropContentEvent(payload as Int)
        Event.SHOW_BACKDROP_CONTENT               -> handleShowBackdropContentEvent(payload as Int)
        Event.HIDE_BACKDROP_CONTENT               -> handleHideBackdropContentEvent()
        Event.BACKDROP_CONTENT_VISIBLE            -> onBackdropContentVisible(payload as View)
        Event.BACKDROP_CONTENT_INVISIBLE          -> onBackdropContentInvisible()

        // toolbar events
        Event.CHANGE_NAVIGATION_ITEM              -> handleToolbarItemChangedEvent(payload as BackdropToolbarItem)
        Event.PRIMARY_ACTION_TRIGGERED            -> onPrimaryActionClicked()
        Event.MORE_ACTION_TRIGGERED               -> onMoreActionClicked()
        Event.START_ACTION_MODE                   -> handleToolbarActionModeStart(payload as BackdropToolbarItem)
        Event.FINISH_ACTION_MODE                  -> handleToolbarActionModeFinish()
        Event.PRIMARY_ACTION_ACTIONMODE_TRIGGERED -> onPrimaryActionInActionModeClicked()
        Event.MORE_ACTION_ACTIONMODE_TRIGGERED    -> onMoreActionInActionModeClicked()
        Event.ACTION_MODE_FINISHED                -> onToolbarActionModeFinished()

        // card stack events
        Event.ADD_TOP_CARD                        -> handleAddTopCardEvent(payload as CardBackdropFragment)
        Event.REMOVE_TOP_CARD                     -> handleRemoveTopCardEvent()

        // fullscreen
        Event.SHOW_FULLSCREEN_FRAGMENT            -> handleShowFullscreenFragmentEvent(payload as FullscreenBackdropFragment)
        Event.REVEAL_FULLSCREEN_FRAGMENT          -> handleRevealFullscreenFragmentEvent(payload as FullscreenRevealBackdropFragment)
        Event.HIDE_FULLSCREEN_FRAGMENT            -> handleHideFullscreenFragmentEvent()
    }
}

//-----------------------------------------
// backdrop content event handling
//-----------------------------------------

private fun BackdropActivity.handlePrefetchBackdropContentEvent(layoutResId: Int): Boolean {
    backdropContent.preCacheContentView(layoutResId)
    return true
}

private fun BackdropActivity.handleShowBackdropContentEvent(layoutResId: Int): Boolean {
    backdropContent.showContentView(layoutResId) { contentView ->
        backdropToolbar.disableActions()
        backdropToolbar.showBackdropCloseButton()
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
    backdropToolbar.showMenuButton()
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

//-----------------------------------------
// card stack event handling
//-----------------------------------------

private fun BackdropActivity.handleAddTopCardEvent(cardFragment: CardBackdropFragment): Boolean {
    val toolbarItem = cardFragment.toolbarItem
    backdropCardStack.push(cardFragment)
    backdropToolbar.configure(toolbarItem, BackdropToolbarMainButtonState.BACK)
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