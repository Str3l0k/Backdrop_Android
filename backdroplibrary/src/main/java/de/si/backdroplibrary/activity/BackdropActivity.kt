package de.si.backdroplibrary.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_ANIMATION_DURATION
import de.si.backdroplibrary.Backdrop.Companion.BACKDROP_CLOSED_TRANSLATION_Y
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.BackdropViewModel
import de.si.backdroplibrary.R
import de.si.backdroplibrary.children.MainCardBackdropFragment
import de.si.backdroplibrary.components.BackdropCardStack
import de.si.backdroplibrary.components.BackdropContent
import de.si.backdroplibrary.components.FullscreenDialogs
import de.si.backdroplibrary.components.toolbar.BackdropToolbar
import de.si.backdroplibrary.gestures.BackdropGestureNavigationListener
import kotlinx.android.synthetic.main.layout_main.*

abstract class BackdropActivity : AppCompatActivity(), BackdropComponent {

    //-----------------------------------------
    // viewModel
    //-----------------------------------------
    override val backdropViewModel by lazy {
        BackdropViewModel.registeredInstance(activity = this)
    }

    override val backdropActivity: BackdropActivity = this

    //-----------------------------------------
    //  backdrop components
    //-----------------------------------------
    internal lateinit var backdropToolbar: BackdropToolbar
    internal lateinit var backdropContent: BackdropContent
    internal lateinit var backdropCardStack: BackdropCardStack
    internal lateinit var fullscreenDialogs: FullscreenDialogs
    internal lateinit var gestureNavigationListener: BackdropGestureNavigationListener
    internal lateinit var gestureDetector: GestureDetector

    //-----------------------------------------
    // properties
    //-----------------------------------------
    abstract val baseCardFragment: MainCardBackdropFragment
    internal var menuLayoutRes: Int = 0

    private val backdropOpen: Boolean
        get() = backdropCardStack.isTranslatedByY

    //-----------------------------------------
    // animation properties
    //-----------------------------------------
    private val backdropOpenCloseAnimator by lazy {
        ObjectAnimator.ofFloat(layout_backdrop_cardstack, View.TRANSLATION_Y, 0f).apply {
            duration = BACKDROP_ANIMATION_DURATION
        }
    }

    //-----------------------------------------
    // lifecycle
    //-----------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

        initializeViewModel()
        initializeComponents()
        initializeBaseCardFragment()
        initializeBaseToolbarItem()
        initializeGestureNavigation()
    }

    override fun onRestart() {
        super.onRestart()
        initializeViewModel()
    }

    override fun onStop() {
        super.onStop()
        backdropViewModel.unregisterEventCallbacks(backdropComponent = this)
    }

    override fun onBackPressed() {
        when {
            fullscreenDialogs.isVisible           -> fullscreenDialogs.hideFullscreenFragment()
            backdropOpen                          -> hideBackdropContent()
            backdropToolbar.isInActionMode        -> finishToolbarActionMode()
            backdropCardStack.hasMoreThanOneEntry -> removeTopCardFragment()
            else                                  -> super.onBackPressed()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onMenuActionClicked(): Boolean {
        showBackdropContent(menuLayoutRes)
        return true
    }

    //-----------------------------------------
    // Animation functions
    //-----------------------------------------
    internal fun animateBackdropOpening(translationY: Float) {
        backdropOpenCloseAnimator.setFloatValues(translationY)
        backdropOpenCloseAnimator.start()
    }

    internal fun animateBackdropClosing() {
        backdropOpenCloseAnimator.setFloatValues(BACKDROP_CLOSED_TRANSLATION_Y)
        backdropOpenCloseAnimator.start()
    }

    fun setMenuLayout(@LayoutRes layoutResId: Int) {
        menuLayoutRes = layoutResId
        prefetchBackdropContent(layoutResId)
    }

    //-----------------------------------------
    // Helper
    //-----------------------------------------
    private fun initializeViewModel() {
        backdropViewModel.registerEventCallback(backdropComponent = this, callbackBackdrop = this::onEvent)
    }

    private fun initializeComponents() {
        backdropContent = BackdropContent(backdropActivity = this)
        backdropCardStack = BackdropCardStack(backdropActivity = this)
        backdropToolbar = BackdropToolbar(backdropActivity = this)
        fullscreenDialogs = FullscreenDialogs(backdropActivity = this)
    }

    private fun initializeBaseCardFragment() {
        backdropCardStack.baseFragment = baseCardFragment
    }

    private fun initializeBaseToolbarItem() {
        backdropToolbar.configure(baseCardFragment.toolbarItem, baseCardFragment.menuButtonState)
    }

    private fun initializeGestureNavigation() {
        gestureNavigationListener = BackdropGestureNavigationListener()
        gestureDetector = GestureDetector(applicationContext, gestureNavigationListener)

        gestureNavigationListener.onFlingDownCallback = {
            if (backdropViewModel.gestureNavigationEnabled) {
                if (backdropCardStack.hasMoreThanOneEntry) {
                    removeTopCardFragment()
                } else if (backdropToolbar.menuButtonVisible && backdropOpen.not() && menuLayoutRes > 0) {
                    showBackdropContent(menuLayoutRes)
                }
            }
        }
    }
}
