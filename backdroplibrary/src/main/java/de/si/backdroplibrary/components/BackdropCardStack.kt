package de.si.backdroplibrary.components

import android.util.Log
import androidx.fragment.app.FragmentManager
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.CardBackdropFragment
import de.si.kotlinx.add
import de.si.kotlinx.realPixelsFromDensityPixels
import de.si.kotlinx.remove
import kotlinx.android.synthetic.main.layout_main.*
import java.util.*

internal class BackdropCardStack(override val backdropActivity: BackdropActivity) : BackdropComponent {

    // view elements
    private val layoutContainer = backdropActivity.layout_backdrop_cardstack

    // properties
    private val fragmentManager: FragmentManager = backdropActivity.supportFragmentManager

    // stack
    private val fragmentStack: Stack<CardBackdropFragment> = Stack()

    internal val isTranslatedByY: Boolean
        get() = layoutContainer.translationY > 0

    internal val hasMoreThanOneEntry
        get() = fragmentStack.count() > 1

    internal var baseFragment
        get() = fragmentStack[0]
        set(value) {
            fragmentStack.add(0, value)
            fragmentManager.add(value, layoutContainer.id)
        }

    internal val topFragment
        get() = fragmentStack.peek()

    internal fun push(fragment: CardBackdropFragment) {
        printCountWarningIfNecessary()
        fragment.cardTopMargin = newTopCardMargin()
        topFragment.hideContent()
        fragmentStack.push(fragment)
        addNewFragment(fragment)
    }

    internal fun pop() {
        val recentTopFragment = fragmentStack.pop()
        topFragment.showContent()
        fragmentManager.remove(recentTopFragment)
    }

    internal fun disable() {
        fragmentStack.forEach(CardBackdropFragment::disable)
    }

    internal fun enable() {
        fragmentStack.forEach(CardBackdropFragment::enable)
    }

    private fun newTopCardMargin(): Int {
        return fragmentStack.size * 8.realPixelsFromDensityPixels(backdropActivity.applicationContext)
    }

    private fun addNewFragment(fragment: CardBackdropFragment) {
        fragmentManager.add(fragment, layoutContainer.id)
    }

    private fun printCountWarningIfNecessary() {
        if (fragmentStack.count() > 3) {
            Log.w("Backdrop_Card_Stack", "Recommended maximum count already reached before (is 3).")
        }
    }
}
