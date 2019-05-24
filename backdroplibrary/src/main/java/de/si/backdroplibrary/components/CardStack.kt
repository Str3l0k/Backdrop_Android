package de.si.backdroplibrary.components

import android.util.Log
import androidx.fragment.app.FragmentManager
import de.si.backdroplibrary.Component
import de.si.backdroplibrary.activity.Activity
import de.si.backdroplibrary.children.CardFragment
import de.si.kotlinx.add
import de.si.kotlinx.realPixelsFromDensityPixels
import de.si.kotlinx.remove
import kotlinx.android.synthetic.main.backdrop_base.*
import java.util.*

class CardStack(override val activity: Activity) : Component {

    // view elements
    private val layoutContainer = activity.layout_backdrop_cardstack

    // properties
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    // stack
    private val fragmentStack: Stack<CardFragment> = Stack()

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

    internal val newTopCardMargin
        get() = fragmentStack.size * 8.realPixelsFromDensityPixels(activity.applicationContext)

    internal fun push(fragment: CardFragment) {
        printCountWarningIfNecessary()
        fragment.cardTopMargin = newTopCardMargin
        topFragment.hideContent()
        fragmentStack.push(fragment)
        addNewFragment(fragment)
    }

    private fun addNewFragment(fragment: CardFragment) {
        fragmentManager.add(fragment, layoutContainer.id)
    }

    internal fun pop() {
        val recentTopFragment = fragmentStack.pop()
        topFragment.showContent()
        fragmentManager.remove(recentTopFragment)
    }

    internal fun disable() {
        fragmentStack.forEach { fragment ->
            fragment.disable()
        }
    }

    internal fun enable() {
        fragmentStack.forEach { fragment ->
            fragment.enable()
        }
    }

    private fun printCountWarningIfNecessary() {
        if (fragmentStack.count() > 3) {
            Log.w("Backdrop_Card_Stack", "Recommended maximum count already reached before (is 3).")
        }
    }
}