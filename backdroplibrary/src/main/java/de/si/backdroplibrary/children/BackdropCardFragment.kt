package de.si.backdroplibrary.children

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import de.si.backdroplibrary.R
import kotlinx.android.synthetic.main.backdrop_card_layout.view.*
import kotlin.random.Random

class BackdropCardFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflatedLayout = inflater.inflate(R.layout.backdrop_card_layout, container, false)
        arguments?.let { bundle ->
            val cardTopMargin = bundle.getInt("top_margin", 0)
            inflatedLayout.backdrop_basecard.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.topMargin = cardTopMargin
            }
        }

        inflatedLayout.backdrop_basecard_content.setBackgroundColor(
            Color.rgb(
                Random.nextInt(255),
                Random.nextInt(255),
                Random.nextInt(255)
            )
        )

        return inflatedLayout
    }

    // TODO add abstract method to inflate content
}