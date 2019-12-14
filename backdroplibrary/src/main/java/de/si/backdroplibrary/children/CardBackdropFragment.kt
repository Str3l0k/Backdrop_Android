package de.si.backdroplibrary.children

import android.os.Bundle
import android.transition.Slide
import android.view.*
import androidx.core.view.get
import androidx.core.view.isVisible
import de.si.backdroplibrary.Backdrop
import de.si.backdroplibrary.R
import de.si.backdroplibrary.components.toolbar.BackdropToolbarItem
import de.si.backdroplibrary.gestures.BackdropGestureNavigationListener
import de.si.kotlinx.fadeInAnimator
import de.si.kotlinx.fadeOut
import de.si.kotlinx.setTopMargin
import kotlinx.android.synthetic.main.layout_card_fragment.view.*

abstract class CardBackdropFragment : BackdropFragment() {

    var cardTopMargin: Int
        get() = arguments?.getInt("top_margin", 0) ?: 0
        set(value) {
            arguments = Bundle().apply {
                putInt("top_margin", value)
            }
        }

    internal val gestureNavigationListener = BackdropGestureNavigationListener()
    internal val gestureDetector: GestureDetector by lazy { GestureDetector(requireContext(), gestureNavigationListener) }

    // additional construction
    init {
        enterTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
        exitTransition = Slide(Gravity.BOTTOM).setDuration(Backdrop.BACKDROP_ANIMATION_DURATION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeGestureNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflatedLayout = inflateMainLayout(inflater, container)
        inflatedLayout?.view_cardstack_card?.setTopMargin(cardTopMargin)
        inflatedLayout?.layout_cardstack_fragment_content?.addView(onCreateContentView(inflater,
                                                                                       container,
                                                                                       savedInstanceState),
                                                                   ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                          ViewGroup.LayoutParams.MATCH_PARENT))
        return inflatedLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.layout_cardstack_blocklayer.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
        val contentView = view.layout_cardstack_fragment_content[0]
        onContentViewCreated(contentView, savedInstanceState)
    }

    private fun inflateMainLayout(inflater: LayoutInflater, container: ViewGroup?): ViewGroup? {
        return inflater.inflate(R.layout.layout_card_fragment, container, false) as? ViewGroup
    }

    private fun initializeGestureNavigation() {
        gestureNavigationListener.onFlingUpCallback = {
            if (backdropViewModel.gestureNavigationEnabled) {
                hideBackdropContent()
            }
        }
    }
    //-----------------------------------------
    // endregion
    //-----------------------------------------

    //-----------------------------------------
    // region backdrop control
    //-----------------------------------------
    internal fun disable() {
        view?.layout_cardstack_blocklayer?.isVisible = true
        val fadeInAnimator = view?.layout_cardstack_blocklayer?.fadeInAnimator
        fadeInAnimator?.duration = Backdrop.BACKDROP_ANIMATION_DURATION
        fadeInAnimator?.setFloatValues(0.42f)
        fadeInAnimator?.start()
    }

    internal fun enable() {
        view?.layout_cardstack_blocklayer?.fadeOut(duration = Backdrop.BACKDROP_ANIMATION_DURATION) {
            view?.layout_cardstack_blocklayer?.isVisible = false
        }
    }

    internal fun hideContent() {
        view?.layout_cardstack_fragment_content?.fadeOut(duration = Backdrop.BACKDROP_ANIMATION_DURATION) {
            view?.layout_cardstack_fragment_content?.isVisible = false
        }
    }

    internal fun showContent() {
        view?.layout_cardstack_fragment_content?.alpha = 1f
        view?.layout_cardstack_fragment_content?.isVisible = true
    }
    //-----------------------------------------
    // endregion
    //-----------------------------------------

    //-----------------------------------------
    // region abstract implementation
    //-----------------------------------------
    abstract fun onCreateContentView(inflater: LayoutInflater,
                                     container: ViewGroup?,
                                     savedInstanceState: Bundle?): View?

    abstract fun onContentViewCreated(view: View?, savedInstanceState: Bundle?)

    abstract val toolbarItem: BackdropToolbarItem
    //-----------------------------------------
    // endregion
    //-----------------------------------------
}