package de.si.kotlinx

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.add(fragment: Fragment, @IdRes containerId: Int) {
    val transaction = beginTransaction()
    transaction.add(containerId, fragment)
    transaction.commit()
}

fun FragmentManager.remove(fragment: Fragment) {
    val transaction = beginTransaction()
    transaction.disallowAddToBackStack()
    transaction.remove(fragment)
    transaction.commit()
}
