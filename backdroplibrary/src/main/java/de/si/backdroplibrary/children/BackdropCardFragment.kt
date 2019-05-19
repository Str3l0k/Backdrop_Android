package de.si.backdroplibrary.children

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.si.backdroplibrary.R
import de.si.kotlinx.setTopMargin
import kotlinx.android.synthetic.main.backdrop_card_layout.view.*

open class BackdropCardFragment : Fragment() {

    var cardTopMargin: Int
        get() = arguments?.getInt("top_margin", 0) ?: 0
        set(value) {
            arguments = Bundle().apply {
                putInt("top_margin", value)
            }
        }

    // construction
    init {
        enterTransition = Slide(Gravity.BOTTOM)
        exitTransition = Slide(Gravity.BOTTOM)
    }

    // view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflatedLayout = inflateMainLayout(inflater, container)
        inflatedLayout?.backdrop_basecard?.setTopMargin(cardTopMargin)
        inflatedLayout?.addView(onCreateContentView(inflater, container, savedInstanceState))
        return inflatedLayout
    }

    private fun inflateMainLayout(inflater: LayoutInflater, container: ViewGroup?): ViewGroup? {
        return inflater.inflate(R.layout.backdrop_card_layout, container, false) as? ViewGroup
    }

    // TODO add abstract method to inflate content
    open fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context)
    }
}