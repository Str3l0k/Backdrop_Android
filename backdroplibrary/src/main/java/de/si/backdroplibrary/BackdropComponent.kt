package de.si.backdroplibrary

import androidx.annotation.LayoutRes
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.BackdropCardFragment
import de.si.backdroplibrary.children.BackdropFullscreenFragment

interface BackdropComponent {
    val activity: BackdropActivity
    val viewModel: BackdropViewModel
        get() = BackdropViewModel.registeredInstance(activity)

    fun showBackdropContent(@LayoutRes layoutResId: Int) {
        viewModel.emit(BackdropEvent.SHOW_BACKDROP_CONTENT, layoutResId)
    }

    fun hideBackdropContent() {
        viewModel.emit(BackdropEvent.HIDE_BACKDROP_CONTENT)
    }

    fun addCardFragment(cardFragment: BackdropCardFragment) {
        viewModel.emit(BackdropEvent.ADD_TOP_CARD, cardFragment)
    }

    fun removeTopCardFragment() {
        viewModel.emit(BackdropEvent.REMOVE_TOP_CARD)
    }

    fun showFullscreenFragment(fragment: BackdropFullscreenFragment) {
        TODO("Not implemented")
    }

    fun hideFullscreenFragment() {
        TODO("Not implemented")
    }

    fun changeTitle(newTitle: String) {
        viewModel.emit(BackdropEvent.CHANGE_TITLE, newTitle)
    }

    fun clearSubtitle() {
        viewModel.emit(BackdropEvent.CLEAR_SUBTITLE)
    }
}