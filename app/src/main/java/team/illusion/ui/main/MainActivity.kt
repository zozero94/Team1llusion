package team.illusion.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.illusion.R
import team.illusion.data.model.Count
import team.illusion.data.model.Member
import team.illusion.data.model.isExpireDate
import team.illusion.ui.admin.AdminActivity
import team.illusion.ui.component.ConfirmButton
import team.illusion.ui.component.MemberColumn
import team.illusion.ui.component.NormalTextField
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
                val openCheckInDialog = rememberSaveable { mutableStateOf<Member?>(null) }
                val scope = rememberCoroutineScope()

                openCheckInDialog.value?.let { checkInMember ->
                    AlertDialog(
                        onDismissRequest = { openCheckInDialog.value = null },
                        backgroundColor = Color.White,
                        confirmButton = {
                            TextButton(onClick = {
                                scope.launch {
                                    viewModel.checkIn(checkInMember)
                                    viewModel.updateId("")
                                }
                            }) {
                                Text(text = stringResource(id = android.R.string.ok), color = Color.Black)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openCheckInDialog.value = null }) {
                                Text(text = stringResource(id = android.R.string.cancel), color = Color.Black)
                            }
                        },
                        text = {
                            Text(
                                text = buildAnnotatedString {
                                    appendLine("${checkInMember.name}(${checkInMember.phone})")
                                    appendLine("시작일 : ${checkInMember.startDate}")
                                    append("종료일 : ")
                                    withStyle(SpanStyle(if (checkInMember.isExpireDate()) Color.Red else Color.Unspecified)) {
                                        appendLine(checkInMember.endDate)
                                    }
                                    append("남은 횟수 : ")
                                    withStyle(SpanStyle(if (checkInMember.remainCount.isExpire()) Color.Red else Color.Unspecified)) {
                                        appendLine("${checkInMember.remainCount}")
                                    }

                                },
                                color = Color.Black
                            )
                        },
                        title = {
                            Text(text = "체크인 하겠습니까??")
                        }
                    )
                }
                LaunchedEffect(Unit) {
                    viewModel.verifyEvent.collectLatest { verifyEvent ->
                        when (verifyEvent) {
                            is VerifyEvent.Confirm -> {
                                openCheckInDialog.value = verifyEvent.member
                            }
                            VerifyEvent.Duplicate -> {
                                sheetState.show()
                            }
                            VerifyEvent.Empty -> {
                                Toast.makeText(this@MainActivity, "데이터 없음", Toast.LENGTH_SHORT).show()
                            }
                            is VerifyEvent.Error -> {
                                Toast.makeText(this@MainActivity, "${verifyEvent.t.message}", Toast.LENGTH_SHORT).show()
                            }
                            is VerifyEvent.CheckIn -> {
                                val count = verifyEvent.remainCount.count
                                val message = "${verifyEvent.name} check in (" +
                                        if (count == null) {
                                            "무제한 이용권"
                                        } else {
                                            "$count->${count - 1}"
                                        } + ")"

                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                openCheckInDialog.value = null
                                sheetState.hide()
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
                            clickMember = { openCheckInDialog.value = it }
                        )
                    }
                ) {
                    MainScreen(uiState.memberIdentifier) { event ->
                        when (event) {
                            MainEvent.ClickAdmin -> startActivity(AdminActivity.getIntent(this@MainActivity))
                            MainEvent.Confirm -> viewModel.verify()
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
    data class Confirm(val member: Member) : VerifyEvent
    object Duplicate : VerifyEvent
    data class Error(val t: Throwable) : VerifyEvent
    data class CheckIn(val name: String, val remainCount: Count) : VerifyEvent
    object Empty : VerifyEvent
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
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp)),
                painter = painterResource(id = R.mipmap.profile),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        NormalTextField(
            text = identifier,
            label = "회원번호",
            keyboardType = KeyboardType.Phone,
            onValueChange = { event(MainEvent.ChangeMember(it)) }
        )
        ConfirmButton(text = "확인") {
            event(MainEvent.Confirm)
        }
    }
}

sealed interface MainEvent {
    object ClickAdmin : MainEvent
    object Confirm : MainEvent
    data class ChangeMember(val id: String) : MainEvent
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Team1llusionTheme {
        MainScreen("uiState.memberIdentifier") {}
    }
}
