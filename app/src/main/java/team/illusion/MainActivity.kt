package team.illusion

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import team.illusion.admin.AdminActivity
import team.illusion.admin.member.search.MemberColumn
import team.illusion.component.ConfirmButton
import team.illusion.component.NormalTextField
import team.illusion.data.model.Member
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                // A surface container using the 'background' color from the theme
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true
                )
                LaunchedEffect(Unit) {
                    viewModel.verifyEvent.collectLatest { verifyEvent ->
                        when (verifyEvent) {
                            VerifyEvent.Confirm -> {
                                Toast.makeText(this@MainActivity, "ok", Toast.LENGTH_SHORT).show()
                            }
                            VerifyEvent.Duplicate -> {
                                sheetState.show()
                            }
                            VerifyEvent.Error -> {
                                Toast.makeText(this@MainActivity, "에러", Toast.LENGTH_SHORT).show()
                            }
                            null -> {
                                //do nothing
                            }
                        }
                    }
                }

                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = {
                        MemberColumn(
                            modifier = Modifier.padding(16.dp),
                            members = uiState.members,
                            clickMember = { viewModel.checkIn(it) })
                    }
                ) {
                    MainScreen(uiState.memberIdentifier) { event ->
                        when (event) {
                            MainEvent.ClickAdmin -> startActivity(AdminActivity.getIntent(this@MainActivity))
                            MainEvent.ClickMember -> viewModel.verify()
                            is MainEvent.ChangeMember -> viewModel.updateId(event.id)
                        }

                    }
                }
            }
        }
    }

}

data class MainUiState(
    val memberIdentifier: String,
    val members: List<Member>,
)

sealed interface VerifyEvent {
    object Confirm : VerifyEvent
    object Duplicate : VerifyEvent
    object Error : VerifyEvent
}

@Composable
fun MainScreen(identifier: String, event: (MainEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { event(MainEvent.ClickAdmin) }) {
            Image(painter = painterResource(id = R.mipmap.profile), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(16.dp))
        NormalTextField(
            text = identifier,
            label = "회원번호",
            keyboardType = KeyboardType.Phone,
            onValueChange = { event(MainEvent.ChangeMember(it)) }
        )
        ConfirmButton(text = "확인") {
            event(MainEvent.ClickMember)
        }
    }
}

sealed interface MainEvent {
    object ClickAdmin : MainEvent
    object ClickMember : MainEvent
    data class ChangeMember(val id: String) : MainEvent
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Team1llusionTheme {
        MainScreen("uiState.memberIdentifier") {}
    }
}
