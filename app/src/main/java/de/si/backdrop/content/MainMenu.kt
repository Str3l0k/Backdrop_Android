package de.si.backdrop.content

import android.view.View
import de.si.backdrop.R
import de.si.backdrop.children.FullscreenDialogBackdropFragment
import de.si.backdrop.children.FullscreenRevealBackdropFragment
import de.si.backdroplibrary.BackdropComponent
import de.si.kotlinx.globalCenterPoint
import kotlinx.android.synthetic.main.main_menu.view.*

class MainMenu(view: View, private val backdropBackdropComponent: BackdropComponent) {

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

    @Suppress("UNUSED_PARAMETER")
    private fun changeContent(view: View) {
        backdropBackdropComponent.showBackdropContent(R.layout.test_content)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun showFullscreenFragmentSlide(view: View) {
        backdropBackdropComponent.showFullscreenFragment(FullscreenDialogBackdropFragment())
    }

    private fun showFullscreenFragmentReveal(view: View) {
        val centerPoint = view.globalCenterPoint
        backdropBackdropComponent.revealFullscreenFragment(FullscreenRevealBackdropFragment(), centerPoint, centerPoint)
    }
}