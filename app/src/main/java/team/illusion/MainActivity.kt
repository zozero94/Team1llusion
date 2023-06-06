package team.illusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.admin.AdminActivity
import team.illusion.member.MemberActivity
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                // A surface container using the 'background' color from the theme
                MainScreen() { event ->
                    when (event) {
                        MainEvent.ClickAdmin -> startActivity(AdminActivity.getIntent(this@MainActivity))
                        MainEvent.ClickMember -> startActivity(MemberActivity.getIntent(this@MainActivity))
                    }

                }
            }
        }
    }

}

@Composable
fun MainScreen(event: (MainEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = { event(MainEvent.ClickAdmin) }) {
            Text(text = "관리자")
        }
        TextButton(onClick = { event(MainEvent.ClickMember) }) {
            Text(text = "회원")
        }
    }
}

sealed interface MainEvent {
    object ClickAdmin : MainEvent
    object ClickMember : MainEvent
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Team1llusionTheme {
        MainScreen {}
    }
}
