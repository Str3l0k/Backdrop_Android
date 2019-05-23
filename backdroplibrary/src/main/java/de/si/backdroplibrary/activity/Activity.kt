package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.Component
import de.si.backdroplibrary.Event
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.CardFragment
import de.si.backdroplibrary.components.CardStack
import de.si.backdroplibrary.components.Content
import de.si.backdroplibrary.components.Toolbar
import kotlinx.android.synthetic.main.backdrop_base.*

abstract class Activity : AppCompatActivity(), Component {

    // viewModel
    override val viewModel by lazy {
        BackdropViewModel.registeredInstance(this)
    }

    override val activity: Activity = this

    /* backdrop components */
    internal lateinit var toolbar: Toolbar
    internal lateinit var content: Content
    internal lateinit var cardStack: CardStack

    abstract val baseCardFragment: CardFragment

    internal val open: Boolean
        get() = cardStack.isTranslatedByY

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
        initializeBaseCardFragment()
        initializeBaseToolbarItem()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterEventCallbacks(this::onEvent)
    }

    override fun onBackPressed() {
        when {
            // TODO fullscreen -> hide
            open -> {
                hideBackdropContent()
            }
            cardStack.hasMoreThanOneEntry -> {
                removeTopCardFragment()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun initializeViewModel() {
        viewModel.registerEventCallback(this::onEvent)
    }

    private fun initializeComponents() {
        initializeContent()
        initializeCardStack()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        toolbar = Toolbar(this)
        toolbar.closeBackdropClickCallback = {
            hideBackdropContent()
        }
    }

    private fun initializeContent() {
        content = Content(this)
    }

    private fun initializeCardStack() {
        cardStack = CardStack(this)
    }

    private fun initializeBaseCardFragment() {
        cardStack.baseFragment = baseCardFragment
    }

    private fun initializeBaseToolbarItem() {
        toolbar.configure(cardStack.baseFragment.toolbarItem)
    }
    /* endregion lifecycle */

    /* region animation functions*/
    internal fun animateBackdropOpening(translationY: Float) {
        backdropOpenCloseAnimator.setFloatValues(translationY)
        backdropOpenCloseAnimator.start()
    }

    internal fun animateBackdropClosing() {
        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
    }
    /* endregion animation functions */

    /* region main menu */
    fun setMenuButtonClickCallback(clickCallback: () -> Unit) {
        toolbar.openMenuClickCallback = clickCallback
    }

    fun setMenuButtonLongClickCallback(longClickCallback: () -> Boolean) {
        toolbar.openMenuLongClickCallback = longClickCallback
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        viewModel.emit(Event.PREFETCH_BACKDROP_CONTENT_VIEW, layoutResId)
        toolbar.openMenuClickCallback = {
            showBackdropContent(layoutResId)
        }
    }
    /* endregion */
}