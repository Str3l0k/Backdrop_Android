package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.R
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.BackdropToolbar
import kotlinx.android.synthetic.main.backdrop_base.*

abstract class BackdropActivity : AppCompatActivity() {

    // viewmodel
    protected val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    /* backdrop components */
    internal lateinit var toolbar: BackdropToolbar
    internal lateinit var content: BackdropContent
    internal lateinit var cardStack: BackdropCardStack

    /* backdrop state information */
    protected val backdropContentInvisible: Boolean
        get() = layout_backdrop_cardstack.translationY.toInt() == BACKDROP_CLOSED_TRANSLATION_Y.toInt()

    /* animation properties */
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
        }
    }

    /* region lifecycle */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backdrop_base)
        initializeViewModel()
        initializeComponents()
    }

    private fun initializeViewModel() {
        viewModel.registerEventCallback(this::onEvent)
    }

    private fun initializeComponents() {
        initializeToolbar()
        initializeContent()
    }

    private fun initializeToolbar() {
        toolbar = BackdropToolbar(this)
        toolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
    }

    private fun initializeContent() {
        content = BackdropContent(this)
    }
    /* endregion lifecycle */

    /* user related event handling */
    open fun onEventReceived(event: BackdropEvent, payload: Any?): Boolean {
        return false
    }
    /* endregion */

    /* region internal */
    private fun animateBackdropOpening(translationY: Float) {
        backdropOpenCloseAnimator.setFloatValues(translationY)
        backdropOpenCloseAnimator.start()
        toolbar.showBackdropCloseButton()
    }

    private fun animateBackdropClosing() {
        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
        toolbar.showMenuButton()
    }
    /* endregion internal */

    /* region backdrop content control */
    internal fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        content.preCacheContentView(layoutResId)
    }

    internal fun showBackdropContent(@LayoutRes layoutResId: Int) {
        content.setContentView(layoutResId) { contentView ->
            animateBackdropOpening(contentView.height.toFloat())
        }
    }

    internal fun hideBackdropContent() {
        animateBackdropClosing()
        content.hide()
        viewModel.emit(BackdropEvent.BACKDROP_CONTENT_INVISIBLE)
    }
    /* endregion backdrop content control */

    /* region main menu */
    fun setMenuButtonClickCallback(clickCallback: () -> Unit) {
        toolbar.openMenuClickCallback = clickCallback
    }

    fun setMenuButtonLongClickCallback(longClickCallback: () -> Boolean) {
        toolbar.openMenuLongClickCallback = longClickCallback
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        prefetchBackdropContent(layoutResId)
        toolbar.openMenuClickCallback = {
            showBackdropContent(layoutResId)
        }
    }
    /* endregion */
}