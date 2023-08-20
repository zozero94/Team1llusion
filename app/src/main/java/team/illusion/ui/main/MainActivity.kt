package team.illusion.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.illusion.AlertManager
import team.illusion.Sound
import team.illusion.data.model.Count
import team.illusion.data.model.Member
import team.illusion.data.model.isExpireDate
import team.illusion.ui.admin.AdminActivity
import team.illusion.ui.component.MemberColumn
import team.illusion.ui.theme.Team1llusionTheme
import team.illusion.util.showToast
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var alertManager: AlertManager

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        when (result.resultCode) {
            RESULT_CANCELED -> {
                showToast("로그인 취소")
            }

            RESULT_OK -> {
                lifecycleScope.launch {
                    runCatching {
                        val isLogin = viewModel.login(result.data)
                        if (isLogin) {
                            startActivity(AdminActivity.getIntent(this@MainActivity))
                        } else {
                            showToast("인증 실패")
                        }
                    }.onFailure {
                        Timber.e(it)
                        showToast(it.message)
                    }
                }


            }
        }
    }

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
                                showToast("데이터 없음")
                                alertManager.play(Sound.Error)
                            }
                            is VerifyEvent.Error -> {
                                showToast("${verifyEvent.t.message}")
                                alertManager.play(Sound.Error)

                            }
                            is VerifyEvent.CheckIn -> {
                                val count = verifyEvent.remainCount.count
                                val message = "${verifyEvent.name} check in (" +
                                        if (count == null) {
                                            "무제한 이용권"
                                        } else {
                                            "$count->${count - 1}"
                                        } + ")"

                                showToast(message)
                                openCheckInDialog.value = null
                                sheetState.hide()
                                alertManager.play(Sound.Confirm)
                            }

                            VerifyEvent.Logout -> showToast("logout")
                        }
                    }
                }

                ModalBottomSheetLayout(
                    modifier = Modifier.background(color = Color.White),
                    sheetState = sheetState,
                    sheetContent = {
                        MemberColumn(
                            modifier = Modifier.padding(16.dp),
                            members = uiState.members,
                            clickMember = { openCheckInDialog.value = it }
                        )
                    },
                    sheetShape = RoundedCornerShape(12.dp),
                    scrimColor = Color.Gray
                ) {
                    MainScreen(identifier = uiState.memberIdentifier) { event ->
                        when (event) {
                            MainEvent.ClickAdmin -> {
                                val lastAccount = GoogleSignIn.getLastSignedInAccount(this)
                                if (lastAccount != null) {
                                    startActivity(AdminActivity.getIntent(this@MainActivity))
                                } else {
                                    googleSignInLauncher.launch(viewModel.signInIntent)
                                }
                            }

                            MainEvent.Confirm -> viewModel.verify()
                            is MainEvent.InputId -> viewModel.updateId(event.id)
                            MainEvent.Logout -> viewModel.logout()
                            MainEvent.Delete -> viewModel.deleteId()
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

    object Logout : VerifyEvent
}

sealed interface MainEvent {
    object ClickAdmin : MainEvent
    object Confirm : MainEvent
    data class InputId(val id: String) : MainEvent
    object Delete : MainEvent

    object Logout : MainEvent
}

