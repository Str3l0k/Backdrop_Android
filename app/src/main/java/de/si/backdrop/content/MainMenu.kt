package de.si.backdrop.content

import android.view.View
import de.si.backdrop.R
import de.si.backdrop.children.FullscreenDialogFragment
import de.si.backdrop.children.FullscreenRevealFragment
import de.si.backdroplibrary.Component
import de.si.kotlinx.globalCenterPoint
import kotlinx.android.synthetic.main.main_menu.view.*

class MainMenu(
    view: View,
    private val backdropComponent: Component
) {
    companion object {
        internal const val resourceId = R.id.menu_main_layout
    }

    private val buttonChangeContent = view.button_menu_main_change_content
    private val buttonFullscreenFragmentSlide = view.button_menu_main_fullscreen_slide
    private val buttonFullscreenFragmentReveal = view.button_menu_main_fullscreen_reveal

    init {
        buttonChangeContent.setOnClickListener(this::changeContent)
        buttonFullscreenFragmentSlide.setOnClickListener(this::showFullscreenFragmentSlide)
        buttonFullscreenFragmentReveal.setOnClickListener(this::showFullscreenFragmentReveal)
    }

    private fun changeContent(view: View) {
        backdropComponent.showBackdropContent(R.layout.test_content)
    }

    private fun showFullscreenFragmentSlide(view: View) {
        backdropComponent.showFullscreenFragment(FullscreenDialogFragment())
    }

    private fun showFullscreenFragmentReveal(view: View) {
        val centerPoint = view.globalCenterPoint
        val fullscreenRevealFragment = FullscreenRevealFragment().apply {
            revealEpiCenter = centerPoint
        }
        backdropComponent.revealFullscreenFragment(fullscreenRevealFragment)
    }
}