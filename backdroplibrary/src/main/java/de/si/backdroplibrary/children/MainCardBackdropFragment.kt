package de.si.backdroplibrary.children

import de.si.backdroplibrary.components.BackdropToolbarMainButtonState

abstract class MainCardBackdropFragment : CardBackdropFragment() {
    abstract val menuButtonState: BackdropToolbarMainButtonState
}