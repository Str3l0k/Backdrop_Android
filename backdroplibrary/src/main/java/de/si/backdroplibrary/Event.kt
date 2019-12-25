package de.si.backdroplibrary

internal enum class Event {

    // Backdrop content
    PREFETCH_BACKDROP_CONTENT_VIEW,
    SHOW_BACKDROP_CONTENT,
    HIDE_BACKDROP_CONTENT,
    BACKDROP_CONTENT_VISIBLE,
    BACKDROP_CONTENT_INVISIBLE,

    // toolbar
    PRIMARY_ACTION_TRIGGERED,
    MORE_ACTION_TRIGGERED,
    CHANGE_NAVIGATION_ITEM,
    START_ACTION_MODE,
    FINISH_ACTION_MODE,
    PRIMARY_ACTION_ACTIONMODE_TRIGGERED,
    MORE_ACTION_ACTIONMODE_TRIGGERED,
    ACTION_MODE_FINISHED,
    MENU_ACTION_TRIGGERED,

    // card stack
    ADD_TOP_CARD,
    REMOVE_TOP_CARD,

    // fullscreen
    SHOW_FULLSCREEN_FRAGMENT,
    REVEAL_FULLSCREEN_FRAGMENT,
    HIDE_FULLSCREEN_FRAGMENT
}
