package team.illusion.admin.member.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.data.model.Member
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class MemberSearchActivity : ComponentActivity() {

    private val viewModel by viewModels<MemberSearchViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MemberSearchScreen(
                    uiState = uiState
                ) { viewModel.query(it) }
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MemberSearchActivity::class.java)
    }

}


data class MemberSearchUiState(
    val query: String,
    val members: List<Member>,
    val searched: List<Member>
)