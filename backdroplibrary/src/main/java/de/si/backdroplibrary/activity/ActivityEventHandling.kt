package de.si.backdroplibrary.activity

import android.view.View
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.children.CardFragment

internal fun Activity.onEvent(event: Event, payload: Any?): Boolean {
    return when (event) {
        // content events
        Event.PREFETCH_BACKDROP_CONTENT_VIEW -> handlePrefetchBackdropContentEvent(payload)
        Event.SHOW_BACKDROP_CONTENT -> handleShowBackdropContentEvent(payload)
        Event.HIDE_BACKDROP_CONTENT -> handleHideBackdropContentEvent()
        Event.BACKDROP_CONTENT_VISIBLE -> onBackdropContentVisible(payload as View)
        Event.BACKDROP_CONTENT_INVISIBLE -> onBackdropContentInvisible()

        // title events
        Event.CHANGE_TITLE -> handleTitleChangeEvent(payload)
        Event.CLEAR_TITLE -> handleTitleClearEvent()
        Event.CLEAR_SUBTITLE -> handleSubTitleClearEvent()

        // action events
        Event.PRIMARY_ACTION_TRIGGERED -> onPrimaryActionClicked()
        Event.MORE_ACTION_TRIGGERED -> onMoreActionClicked()

        // card stack events
        Event.ADD_TOP_CARD -> handleAddTopCardEvent(payload)
        Event.REMOVE_TOP_CARD -> handleRemoveTopCardEvent()

        else -> false
    }
}

/* helper functions */
private fun isPayloadResourceId(payload: Any?): Int? {
    val payloadInt = payload as? Int ?: -1

    return if (payloadInt > 0) {
        payloadInt
    } else {
        null
    }
}

private fun isPayloadAString(payload: Any?): String? {
    return payload as? String
}

private fun isPayloadCardFragment(payload: Any?): CardFragment? {
    return payload as? CardFragment
}

/* region backdrop content event handling functions */
private fun Activity.handlePrefetchBackdropContentEvent(payload: Any?): Boolean {
    return isPayloadResourceId(payload)?.let { layoutResId ->
        content.preCacheContentView(layoutResId)
        true
    } ?: false
}

private fun Activity.handleShowBackdropContentEvent(payload: Any?): Boolean {
    return isPayloadResourceId(payload)?.let { layoutResId ->
        content.setContentView(layoutResId) { contentView ->
            toolbar.disableActions()
            toolbar.showBackdropCloseButton()
            cardStack.disable()
            animateBackdropOpening(contentView.height.toFloat())
        }
        true
    } ?: false
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

/* region toolbar title event handling functions */
private fun Activity.handleTitleChangeEvent(payload: Any?): Boolean {
    isPayloadAString(payload)?.let { newTitle ->
        toolbar.title = newTitle
    }

    return true
}

private fun Activity.handleTitleClearEvent(): Boolean {
    toolbar.title = null
    return true
}


private fun Activity.handleSubTitleClearEvent(): Boolean {
    toolbar.subTitle = null
    return true
}
/* endregion */

/* region card stack event handling functions */
private fun Activity.handleAddTopCardEvent(payload: Any?): Boolean {
    return isPayloadCardFragment(payload)?.let { backdropCardFragment ->
        backdropCardFragment.toolbarItem.let {
            toolbar.configure(it, true)
        }
        cardStack.push(backdropCardFragment)
        true
    } ?: false
}

private fun Activity.handleRemoveTopCardEvent(): Boolean {
    cardStack.pop()
    toolbar.configure(cardStack.topFragment.toolbarItem, cardStack.hasMoreThanOneEntry)
    return true
}
/* endregion card stack event handling functions */