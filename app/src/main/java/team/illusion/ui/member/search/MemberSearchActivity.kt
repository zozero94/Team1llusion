package team.illusion.ui.member.search

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
import team.illusion.ui.member.info.MemberInfoActivity
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class MemberSearchActivity : ComponentActivity() {

    private val viewModel by viewModels<MemberSearchViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val members by viewModel.members.collectAsStateWithLifecycle()
                val query by viewModel.query.collectAsStateWithLifecycle()
                val filter by viewModel.filter.collectAsStateWithLifecycle()
                MemberSearchScreen(
                    members = members,
                    query = query,
                    filter = filter,
                    queryChanged = viewModel::query
                ) { startActivity(MemberInfoActivity.getIntent(this, it.id)) }
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MemberSearchActivity::class.java)
    }

}


data class MemberSearchUiState(
    val members: List<Member>,
    val searched: List<Member>
)
