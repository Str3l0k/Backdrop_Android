package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.R
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.BackdropToolbar
import de.si.kotlinx.layoutInflater
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

    /* cached backdrop views */
    private val backdropViews: MutableMap<Int, View> = mutableMapOf() // TODO move to content

    /* animation properties */
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
//            doOnEnd {
//                val (event, payload) = if (backdropContentInvisible) {
//                    Pair(BackdropEvent.BACKDROP_CONTENT_INVISIBLE, null)
//                } else {
//                    Pair(BackdropEvent.BACKDROP_CONTENT_VISIBLE, null)
//                }
//
//                viewModel.emit(event)
//            }
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
    }

    private fun initializeToolbar() {
        toolbar = BackdropToolbar(this)
        toolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
    }
    /* endregion lifecycle */

    /* user related event handling */
    open fun onEventReceived(event: BackdropEvent, payload: Any?): Boolean {
        return false
    }
    /* endregion */

    /* region internal */
    private fun inflateView(@LayoutRes layoutResId: Int): View? {
        return applicationContext?.layoutInflater?.inflate(layoutResId, layout_backdrop_content, false)
    }

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
        inflateView(layoutResId)?.let { contentView ->
            backdropViews[layoutResId] = contentView
        }
    }

    internal fun showBackdropContent(@LayoutRes layoutResId: Int) {
        if (backdropViews.containsKey(layoutResId).not()) {
            prefetchBackdropContent(layoutResId)
        }

        val contentView = backdropViews[layoutResId]
        contentView?.doOnNextLayout { contentViewAfterLayout ->
            animateBackdropOpening(contentViewAfterLayout.height.toFloat())
            viewModel.emit(BackdropEvent.BACKDROP_CONTENT_VISIBLE, contentViewAfterLayout)
        }

        layout_backdrop_content.removeAllViews()
        layout_backdrop_content.addView(contentView)
    }

    internal fun hideBackdropContent() {
        animateBackdropClosing()
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