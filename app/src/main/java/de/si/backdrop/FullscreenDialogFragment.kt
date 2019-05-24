package de.si.backdrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdroplibrary.children.FullscreenFragment
import kotlinx.android.synthetic.main.fragment_fullscreen.view.*

class FullscreenDialogFragment : FullscreenFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fullscreen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.toolbar.setNavigationOnClickListener {
            hideFullscreenFragment()
        }
    }
}