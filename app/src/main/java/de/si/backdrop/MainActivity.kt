package de.si.backdrop

import android.os.Bundle
import android.view.View
import de.si.backdroplibrary.BackdropActivity
import de.si.backdroplibrary.BackdropEvent

class MainActivity : BackdropActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setMenuButtonClickListener(View.OnClickListener { _ ->
//            if (backdropContentInvisible) {
                viewModel.emit(BackdropEvent.SHOW_BACKDROP_CONTENT, R.layout.backdrop_test_content)
//            } else {
//                viewModel.emit(BackdropEvent.HIDE_BACKDROP_CONTENT)
//            }
        })

        setMenuButtonLongClickListener(View.OnLongClickListener { _ ->
            println("Menu long click.")
            true
        })

        viewModel.emit(BackdropEvent.PREFETCH_BACKDROP_CONTENT_VIEW, R.layout.backdrop_test_content)
    }
}
