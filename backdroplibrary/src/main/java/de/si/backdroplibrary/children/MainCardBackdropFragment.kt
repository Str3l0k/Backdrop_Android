package de.si.backdroplibrary.children

import de.si.backdroplibrary.components.toolbar.BackdropToolbarMainButtonState

abstract class MainCardBackdropFragment : CardBackdropFragment() {
    abstract val menuButtonState: BackdropToolbarMainButtonState
}