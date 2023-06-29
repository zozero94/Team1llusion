package team.illusion.ui.member

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class MemberActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {

            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MemberActivity::class.java)
    }
}