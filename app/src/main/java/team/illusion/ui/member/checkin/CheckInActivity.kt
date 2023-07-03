package team.illusion.ui.member.checkin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.ui.component.CenterCircularProgressIndicator
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class CheckInActivity : ComponentActivity() {
    private val viewModel by viewModels<CheckInViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val member = viewModel.member.collectAsStateWithLifecycle(initialValue = null).value
                if (member != null) {
                    CheckInScreen(member) { viewModel.deleteCheckIn(it) }
                } else {
                    CenterCircularProgressIndicator()
                }


            }
        }
    }

    companion object {
        const val ID = "id"
        fun getIntent(context: Context, id: String) = Intent(context, CheckInActivity::class.java)
            .putExtra(ID, id)
    }

}


