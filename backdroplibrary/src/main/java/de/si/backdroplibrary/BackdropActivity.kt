package de.si.backdroplibrary

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.BackdropToolbar
import de.si.kotlinx.layoutInflater
import kotlinx.android.synthetic.main.backdrop_base.*

abstract class BackdropActivity : AppCompatActivity() {

    internal companion object {
        const val BACKDROP_CLOSED_TRANSLATION_Y = 0f
        const val BACKDROP_ANIMATION_DURATION = 275L

        val halfBackdropAnimationDuration
            get() = BACKDROP_ANIMATION_DURATION / 2
    }

    // viewmodel
    protected val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    /* backdrop components */
    private lateinit var toolbar: BackdropToolbar
    private lateinit var content: BackdropContent
    private lateinit var cardStack: BackdropCardStack

    /* backdrop state information */
    protected val backdropContentInvisible: Boolean
        get() = layout_backdrop_cardstack.translationY.toInt() == BACKDROP_CLOSED_TRANSLATION_Y.toInt()

    /* cached backdrop views */
    private val backdropViews: MutableMap<Int, View> = mutableMapOf() // TODO move to content

    /* animation properties */
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
            doOnEnd {
                val event = if (backdropContentInvisible) {
                    BackdropEvent.BACKDROP_CONTENT_INVISIBLE
                } else {
                    BackdropEvent.BACKDROP_CONTENT_VISIBLE
                }

                viewModel.emit(event)
            }
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
        viewModel.registerEventCallback(this, this::onEventCallback)
    }

    private fun initializeComponents() {
        initializeToolbar()
    }

    private fun initializeToolbar() {
        toolbar = BackdropToolbar(this)
        toolbar.openMenuClickCallback = {

        }
        toolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
    }
    /* endregion lifecycle */

    /* region backdrop specific event handling */
    private fun onEventCallback(event: BackdropEvent, payload: Any?): Boolean {
        return when (event) {
            BackdropEvent.PREFETCH_BACKDROP_CONTENT_VIEW -> {
                val layoutResId = payload as? Int
                layoutResId?.let {
                    prefetchBackdropContent(layoutResId)
                }
                true
            }
            BackdropEvent.SHOW_BACKDROP_CONTENT -> {
                val layoutResId = payload as? Int
                layoutResId?.let {
                    showBackdropContent(layoutResId)
                }
                true
            }
            BackdropEvent.HIDE_BACKDROP_CONTENT -> {
                hideBackdropContent()
                true
            }
            else -> false
        }
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
    private fun prefetchBackdropContent(@LayoutRes layoutResId: Int) {
        inflateView(layoutResId)?.let { contentView ->
            backdropViews[layoutResId] = contentView
        }
    }

    private fun showBackdropContent(@LayoutRes layoutResId: Int) {
        if (backdropViews.containsKey(layoutResId).not()) {
            prefetchBackdropContent(layoutResId)
        }

        val contentView = backdropViews[layoutResId]
        contentView?.doOnNextLayout {
            animateBackdropOpening(contentView.height.toFloat())
        }

        layout_backdrop_content.removeAllViews()
        layout_backdrop_content.addView(contentView)
    }

    private fun hideBackdropContent() {
        animateBackdropClosing()
    }
    /* endregion backdrop content control */

    /* region main menu */
    fun setMenuButtonClickListener(onClickListener: View.OnClickListener) {
        button_backdrop_toolbar_menu_show.setOnClickListener(onClickListener)
        button_backdrop_toolbar_menu_show.isVisible = true
    }

    fun setMenuButtonLongClickListener(onLongClickListener: View.OnLongClickListener) {
        button_backdrop_toolbar_menu_show.setOnLongClickListener(onLongClickListener)
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        // TODO
    }

    fun setMenuView(view: View) {
        // TODO
    }
    /* endregion */
}