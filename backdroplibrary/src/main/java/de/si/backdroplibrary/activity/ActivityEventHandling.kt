package de.si.backdroplibrary.activity

import android.transition.Transition
import android.view.View
import androidx.core.transition.doOnEnd
import androidx.core.view.isVisible
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.children.FullscreenFragment
import de.si.backdroplibrary.components.ToolbarItem
import de.si.kotlinx.add
import de.si.kotlinx.remove
import kotlinx.android.synthetic.main.backdrop_base.*

internal fun Activity.onEvent(event: Event, payload: Any?): Boolean {
    return when (event) {
        // content events
        Event.PREFETCH_BACKDROP_CONTENT_VIEW -> handlePrefetchBackdropContentEvent(payload as Int)
        Event.SHOW_BACKDROP_CONTENT -> handleShowBackdropContentEvent(payload as Int)
        Event.HIDE_BACKDROP_CONTENT -> handleHideBackdropContentEvent()
        Event.BACKDROP_CONTENT_VISIBLE -> onBackdropContentVisible(payload as View)
        Event.BACKDROP_CONTENT_INVISIBLE -> onBackdropContentInvisible()

        // toolbar events
        Event.CHANGE_NAVIGATION_ITEM -> handleToolbarItemChangedEvent(payload as ToolbarItem)
        Event.PRIMARY_ACTION_TRIGGERED -> onPrimaryActionClicked()
        Event.MORE_ACTION_TRIGGERED -> onMoreActionClicked()

        // card stack events
        Event.ADD_TOP_CARD -> handleAddTopCardEvent(payload as CardFragment)
        Event.REMOVE_TOP_CARD -> handleRemoveTopCardEvent()

        // fullscreen
        Event.SHOW_FULLSCREEN_FRAGMENT -> handleShowFullscreenFragmentEvent(payload as FullscreenFragment)
        Event.HIDE_FULLSCREEN_FRAGMENT -> handleHideFullscreenFragmentEvent()
    }
}

/* region backdrop content event handling */
private fun Activity.handlePrefetchBackdropContentEvent(layoutResId: Int): Boolean {
    content.preCacheContentView(layoutResId)
    return true
}

private fun Activity.handleShowBackdropContentEvent(layoutResId: Int): Boolean {
    content.setContentView(layoutResId) { contentView ->
        toolbar.disableActions()
        toolbar.showBackdropCloseButton()
        cardStack.disable()
        animateBackdropOpening(contentView.height.toFloat())
    }
    return true
}

private fun Activity.handleHideBackdropContentEvent(): Boolean {
    animateBackdropClosing()
    content.hide()
    cardStack.enable()
    toolbar.enableActions()
    toolbar.showMenuButton()
    viewModel.emit(Event.BACKDROP_CONTENT_INVISIBLE)
    return true
}
/* endregion */

/* region toolbar event handling */
private fun Activity.handleToolbarItemChangedEvent(toolbarItem: ToolbarItem): Boolean {
    toolbar.configure(toolbarItem, cardStack.hasMoreThanOneEntry)
    return true
}
/* endregion */

/* region card stack event handling */
private fun Activity.handleAddTopCardEvent(cardFragment: CardFragment): Boolean {
    val toolbarItem = cardFragment.toolbarItem
    toolbar.configure(toolbarItem, cardStack.hasMoreThanOneEntry)
    cardStack.push(cardFragment)
    return true
}

private fun Activity.handleRemoveTopCardEvent(): Boolean {
    cardStack.pop()
    toolbar.configure(cardStack.topFragment.toolbarItem, cardStack.hasMoreThanOneEntry)
    return true
}
/* endregion card stack event handling functions */

/* region fullscreen fragment event handling */
private fun Activity.handleShowFullscreenFragmentEvent(fragment: FullscreenFragment): Boolean {
    layout_backdrop_overlay.isVisible = true
    supportFragmentManager.add(fragment, R.id.layout_backdrop_overlay)
    return true
}

private fun Activity.handleHideFullscreenFragmentEvent(): Boolean {
    val fragment = supportFragmentManager.findFragmentById(R.id.layout_backdrop_overlay)
    (fragment?.exitTransition as Transition).doOnEnd {
        layout_backdrop_overlay.isVisible = false
    }
    fragment.let(supportFragmentManager::remove)

    return true
}
/* endregion */