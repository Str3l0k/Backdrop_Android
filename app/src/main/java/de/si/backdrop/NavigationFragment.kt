package de.si.backdrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.children.BackdropCardFragment

class NavigationFragment : BackdropCardFragment() {
    override fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.base_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            viewModel?.emit(BackdropEvent.ADD_TOP_CARD, NavigationFragment())
        }, 1500)
    }
}