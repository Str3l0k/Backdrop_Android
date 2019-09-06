package de.si.backdrop.children

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdrop.R
import de.si.backdroplibrary.children.FullscreenBackdropFragment
import kotlinx.android.synthetic.main.fragment_fullscreen.view.*

class FullscreenDialogBackdropFragment : FullscreenBackdropFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fullscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.toolbar.navigationIcon?.setTint(Color.WHITE)
        view.toolbar.setNavigationOnClickListener {
            hideFullscreenFragment()
        }
    }
}