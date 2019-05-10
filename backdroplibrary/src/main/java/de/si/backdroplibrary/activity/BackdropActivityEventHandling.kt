package de.si.backdroplibrary.activity

import de.si.backdroplibrary.BackdropEvent

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

        // send everything not consumed to open function for custom implementation
        else -> onEventReceived(event, payload)
    }
}

/* helper functions */
private fun checkPayloadForResourceId(payload: Any?): Int? {
    return payload as? Int
}

private fun checkPayloadForString(payload: Any?): String? {
    return payload as? String
}

/* region backdrop content event handling functions */
private fun BackdropActivity.handlePrefetchBackdropContentEvent(payload: Any?): Boolean {
    return checkPayloadForResourceId(payload)?.let { layoutResId ->
        prefetchBackdropContent(layoutResId)
        true
    } ?: false
}

private fun BackdropActivity.handleShowBackdropContentEvent(payload: Any?): Boolean {
    return checkPayloadForResourceId(payload)?.let { layoutResId ->
        showBackdropContent(layoutResId)
        true
    } ?: false
}

private fun BackdropActivity.handleHideBackdropContentEvent(): Boolean {
    hideBackdropContent()
    return true
}
/* endregion */

/* region toolbar title event handling functions */
private fun BackdropActivity.handleTitleChangeEvent(payload: Any?): Boolean {
    checkPayloadForString(payload)?.let { newTitle ->
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
    checkPayloadForResourceId(payload)?.let { drawableResId ->
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