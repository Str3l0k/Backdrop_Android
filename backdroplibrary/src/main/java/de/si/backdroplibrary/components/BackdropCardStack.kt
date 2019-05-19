package de.si.backdroplibrary.components

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import de.si.backdroplibrary.BackdropComponent
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.children.BackdropCardFragment
import kotlinx.android.synthetic.main.backdrop_base.*
import java.util.*

class BackdropCardStack(activity: BackdropActivity) : BackdropComponent(activity) {

    // view elements
    private val layoutContainer = activity.layout_backdrop_cardstack
    private val layoutTouchBlock = activity.layout_cardstack_touchblock

    // properties
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    // stack
    private val fragmentStack: Stack<BackdropCardFragment> = Stack()

    fun add(fragment: BackdropCardFragment) {
        fragment.arguments = Bundle().apply {
            putInt("top_margin", fragmentStack.count() * 8)
        }
        fragment.enterTransition = Slide(Gravity.BOTTOM)
        fragment.exitTransition = Slide(Gravity.BOTTOM)

        fragmentStack.push(fragment)

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(layoutContainer.id, fragment)
        fragmentTransaction.addToBackStack("backdropcard_${fragmentStack.count()}")
        // TODO remove backstack and implement manual popping
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.commit()
    }

    fun remove(fragment: BackdropCardFragment) {
        fragmentStack.remove(fragment)
    }

    fun push() {

    }

    fun pop() {

    }
}