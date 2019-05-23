package de.si.backdroplibrary.activity

import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.children.BackdropCardFragment

internal fun BackdropActivity.onEvent(event: BackdropEvent, payload: Any?): Boolean {
    return when (event) {
        // content events
        BackdropEvent.PREFETCH_BACKDROP_CONTENT_VIEW -> handlePrefetchBackdropContentEvent(payload)
        BackdropEvent.SHOW_BACKDROP_CONTENT -> handleShowBackdropContentEvent(payload)
        BackdropEvent.HIDE_BACKDROP_CONTENT -> handleHideBackdropContentEvent()

        // title events
        BackdropEvent.CHANGE_TITLE -> handleTitleChangeEvent(payload)
        BackdropEvent.CLEAR_TITLE -> handleTitleClearEvent()
        BackdropEvent.CLEAR_SUBTITLE -> handleSubTitleClearEvent()

        // action events
        BackdropEvent.ACTIVATE_PRIMARY_ACTION -> handleActivatePrimaryActionEvent(payload)
        BackdropEvent.ACTIVATE_MORE_ACTION -> handleActivateMoreActionEvent()
        BackdropEvent.DEACTIVATE_PRIMARY_ACTION -> handleDeactivatePrimaryActionEvent()
        BackdropEvent.DEACTIVATE_MORE_ACTION -> handleDeactivateMoreActionEvent()

        // card stack events
        BackdropEvent.ENABLE_CARDSTACK -> {
            true
        }
        BackdropEvent.DISABLE_CARDSTACK -> {
            true
        }
        BackdropEvent.ADD_TOP_CARD -> handleAddTopCardEvent(payload)
        BackdropEvent.REMOVE_TOP_CARD -> handleRemoveTopCardEvent()

        // send everything not consumed to open function for custom implementation
        else -> onEventReceived(event, payload)
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

private fun isPayloadCardFragment(payload: Any?): BackdropCardFragment? {
    return payload as? BackdropCardFragment
}

/* region backdrop content event handling functions */
private fun BackdropActivity.handlePrefetchBackdropContentEvent(payload: Any?): Boolean {
    return isPayloadResourceId(payload)?.let { layoutResId ->
        content.preCacheContentView(layoutResId)
        true
    } ?: false
}

private fun BackdropActivity.handleShowBackdropContentEvent(payload: Any?): Boolean {
    return isPayloadResourceId(payload)?.let { layoutResId ->
        content.setContentView(layoutResId) { contentView ->
            toolbar.disableActions()
            toolbar.showBackdropCloseButton()
            animateBackdropOpening(contentView.height.toFloat())
        }
        true
    } ?: false
}

private fun BackdropActivity.handleHideBackdropContentEvent(): Boolean {
    animateBackdropClosing()
    content.hide()
    toolbar.enableActions()
    toolbar.showMenuButton()
    viewModel.emit(BackdropEvent.BACKDROP_CONTENT_INVISIBLE)
    return true
}
/* endregion */

/* region toolbar title event handling functions */
private fun BackdropActivity.handleTitleChangeEvent(payload: Any?): Boolean {
    isPayloadAString(payload)?.let { newTitle ->
        toolbar.title = newTitle
    }

    return true
}

private fun BackdropActivity.handleTitleClearEvent(): Boolean {
    toolbar.title = null
    return true
}


private fun BackdropActivity.handleSubTitleClearEvent(): Boolean {
    toolbar.subTitle = null
    return true
}
/* endregion */

/* region action event handling functions */
private fun BackdropActivity.handleActivatePrimaryActionEvent(payload: Any?): Boolean {
    isPayloadResourceId(payload)?.let { drawableResId ->
        toolbar.activatePrimaryAction(drawableResId)
    }

    return true
}

private fun BackdropActivity.handleDeactivatePrimaryActionEvent(): Boolean {
    toolbar.deactivatePrimaryAction()
    return true
}

private fun BackdropActivity.handleActivateMoreActionEvent(): Boolean {
    toolbar.activateMoreAction()
    return true
}

private fun BackdropActivity.handleDeactivateMoreActionEvent(): Boolean {
    toolbar.deactivateMoreAction()
    return true
}
/* endregion */

/* region card stack event handling functions */
private fun BackdropActivity.handleAddTopCardEvent(payload: Any?): Boolean {
    return isPayloadCardFragment(payload)?.let { backdropCardFragment ->
        cardStack.push(backdropCardFragment)
        true
    } ?: false
}

private fun BackdropActivity.handleRemoveTopCardEvent(): Boolean {
    cardStack.pop()
    return true
}
/* endregion card stack event handling functions */