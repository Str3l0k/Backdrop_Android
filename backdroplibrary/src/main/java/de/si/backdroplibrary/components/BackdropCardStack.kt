package de.si.backdroplibrary.components

import androidx.fragment.app.FragmentManager
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.BackdropCardFragment
import kotlinx.android.synthetic.main.backdrop_base.*
import java.util.*

class BackdropCardStack(activity: BackdropActivity) : BackdropComponent(activity) {

    // view elements
    private val layoutContainer = activity.layout_backdrop_cardstack

    // properties
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    // stack
    private val fragmentStack: Stack<BackdropCardFragment> = Stack()

    internal val hasMoreThanOneEntry
        get() = fragmentStack.count() > 1

    internal var baseCardFragment
        get() = fragmentStack[0]
        set(value) {
            fragmentStack.add(0, value)
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(layoutContainer.id, value)
            fragmentTransaction.commit()
        }

    internal fun push(fragment: BackdropCardFragment) {
        fragment.cardTopMargin = fragmentStack.count() * 8 // TODO calculation in DP instead of pixels

        fragmentStack.push(fragment)

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(layoutContainer.id, fragment)
        fragmentTransaction.commit()

        // TODO disable previous views to gone
    }

    internal fun pop() {
        val topFragment = fragmentStack.pop()

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(topFragment)
        fragmentTransaction.commit()
    }
}