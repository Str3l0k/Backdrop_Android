package de.si.backdrop.children

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.FullscreenRevealFragment

class RevealFragment : FullscreenRevealFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.base_card, container, false)
        inflate.setBackgroundColor(Color.WHITE)
        return inflate
    }
}