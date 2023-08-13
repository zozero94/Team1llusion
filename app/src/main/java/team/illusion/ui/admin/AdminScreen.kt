package team.illusion.ui.admin

import android.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import team.illusion.ui.component.ConfirmButton
import team.illusion.ui.component.DeleteItem
import team.illusion.ui.component.PasswordTextField
import team.illusion.ui.component.SettingItem
import team.illusion.ui.component.SettingToggle

val LocalMaxPasswordCount = compositionLocalOf { 10 }

@Composable
fun AdminScreen(uiState: AdminUiState, event: (AdminEvent) -> Unit) {
    val maxPasswordCount = LocalMaxPasswordCount.current
    var password by rememberSaveable(uiState.password) { mutableStateOf(uiState.password) }
    var openDeleteAlert by rememberSaveable { mutableStateOf(false) }

    if (openDeleteAlert) {
        AlertDialog(
            onDismissRequest = { openDeleteAlert = false },
            title = { Text(text = "진짜 다 지움??") },
            confirmButton = {
                TextButton(onClick = {
                    event(AdminEvent.DeleteAll)
                    openDeleteAlert = false
                }) {
                    Text(text = stringResource(id = R.string.ok), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDeleteAlert = false }) {
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
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = "Admin : ${uiState.currentAdminEmail}"
            )
            SettingItem(text = "회원 등록") { event(AdminEvent.ClickMemberRegister) }
            SettingItem(text = "회원 조회") { event(AdminEvent.ClickMemberSearch) }
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

            SettingItem(text = "날짜별 출석 조회") { event(AdminEvent.DateAttendance) }
            DeleteItem(modifier = Modifier, text = "모든 데이터 삭제") { openDeleteAlert = true }
        }
        ConfirmButton(text = "변경") { event(AdminEvent.ChangePassword(password)) }
    }
}

@Preview
@Composable
private fun Preview() {
    AdminScreen(
        uiState = AdminUiState(
            usePassword = true,
            password = "1234",
            currentAdminEmail = "zozero94@gmail.com"
        ),
        event = {}
    )
}

