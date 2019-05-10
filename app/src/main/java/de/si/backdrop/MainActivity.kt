package de.si.backdrop

import android.os.Bundle
import android.view.View
import de.si.backdroplibrary.BackdropEvent
import de.si.backdroplibrary.activity.BackdropActivity
import de.si.backdroplibrary.changeTitle
import de.si.backdroplibrary.clearSubtitle
import kotlin.random.Random

class MainActivity : BackdropActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMenuLayout(R.layout.backdrop_test_content)

        viewModel.emit(BackdropEvent.ACTIVATE_MORE_ACTION)
        viewModel.emit(BackdropEvent.ACTIVATE_PRIMARY_ACTION, R.drawable.abc_ic_commit_search_api_mtrl_alpha)
    }

    override fun onEventReceived(event: BackdropEvent, payload: Any?): Boolean {
        return when (event) {
            BackdropEvent.BACKDROP_CONTENT_VISIBLE -> {
                val view = payload as? View
                view?.let {
                    // TODO configure view
                    println("View received with id ${view.id}")
                    true
                } ?: false
            }
            BackdropEvent.PRIMARY_ACTION_TRIGGERED -> {
                viewModel.changeTitle("Title ${Random.nextInt(42)}")
                viewModel.emit(BackdropEvent.DEACTIVATE_PRIMARY_ACTION)
                true
            }
            BackdropEvent.MORE_ACTION_TRIGGERED -> {
                viewModel.clearSubtitle()
                viewModel.emit(BackdropEvent.DEACTIVATE_MORE_ACTION)
                true
            }
            else -> {
                super.onEventReceived(event, payload)
            }
        }
    }
}
