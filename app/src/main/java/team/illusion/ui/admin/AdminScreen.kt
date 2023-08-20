package team.illusion.ui.admin

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import team.illusion.BuildConfig
import team.illusion.data.model.Center
import team.illusion.ui.component.ConfirmButton
import team.illusion.ui.component.DeleteItem
import team.illusion.ui.component.PasswordTextField
import team.illusion.ui.component.SettingItem
import team.illusion.ui.component.SettingToggle
import team.illusion.ui.theme.IllusionColor

val LocalMaxPasswordCount = compositionLocalOf { 10 }

enum class DeleteOptions(val title: String) {
    ALL("주의 : 전체 데이터가 삭제 됩니다."),
    EXPIRE("기간, 횟수가 모두 소진된 회원을 삭제합니다.")
}
@Composable
fun AdminScreen(uiState: AdminUiState, event: (AdminEvent) -> Unit) {
    val maxPasswordCount = LocalMaxPasswordCount.current
    var password by rememberSaveable(uiState.password) { mutableStateOf(uiState.password) }
    var deleteOptions by rememberSaveable { mutableStateOf<DeleteOptions?>(null) }

    deleteOptions?.let { option ->
        AlertDialog(
            onDismissRequest = { deleteOptions = null },
            title = { Text(text = option.title) },
            confirmButton = {
                TextButton(onClick = {
                    event(AdminEvent.Delete(option))
                    deleteOptions = null
                }) {
                    Text(text = stringResource(id = R.string.ok), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteOptions = null }) {
                    Text(text = stringResource(id = R.string.cancel), color = Color.White)
                }
            },
        )
    }

    LaunchedEffect(key1 = password, block = {
        if (password.length >= maxPasswordCount) {
            event(AdminEvent.OverPasswordLimit(maxPasswordCount))
        }
    })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DebugHeader(adminEmail = uiState.currentAdminEmail)
            SettingItem(
                text = "이용 지점 (${uiState.center.centerName})",
                color = IllusionColor.IllusionYellow,
                click = { event(AdminEvent.OpenCenterDialog) }
            )
            SettingItem(text = "회원 등록", click = { event(AdminEvent.ClickMemberRegister) })
            SettingItem(text = "회원 조회", click = { event(AdminEvent.ClickMemberSearch) })
            SettingToggle(
                text = "비밀번호 설정",
                checked = uiState.usePassword,
                onCheckedChange = { event(AdminEvent.TogglePassword(it)) }
            )
            AnimatedVisibility(visible = uiState.usePassword) {
                PasswordTextField(
                    password = password,
                    isFail = false,
                    onValueChange = { if (it.length <= maxPasswordCount) password = it }
                ) { password = "" }
            }

            SettingItem(text = "날짜별 출석 조회", click = { event(AdminEvent.DateAttendance) })
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeleteItem(text = "기간/횟수 종료된 회원 삭제") { deleteOptions = DeleteOptions.EXPIRE }
            DeleteItem(text = "모든 데이터 삭제") { deleteOptions = DeleteOptions.ALL }
            Spacer(modifier = Modifier.height(20.dp))
            ConfirmButton(text = "변경") { event(AdminEvent.ChangePassword(password)) }
        }

    }
}


@Composable
private fun DebugHeader(
    adminEmail: String,
) {
    var debugVisibility by rememberSaveable { mutableStateOf(false) }
    Column {
        Text(
            modifier = Modifier.clickable { debugVisibility = !debugVisibility },
            text = "Admin : $adminEmail"
        )
        AnimatedVisibility(visible = debugVisibility) {
            Text(
                text = buildAnnotatedString {
                    appendLine()
                    appendLine("hash : ${BuildConfig.GIT_COMMIT_HASH}")
                    append("message : ${BuildConfig.GIT_COMMIT_MESSAGE}")
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AdminScreen(
        uiState = AdminUiState(
            usePassword = true,
            password = "1234",
            currentAdminEmail = "zozero94@gmail.com",
            center = Center.Gangnam
        ),
        event = {}
    )
}

