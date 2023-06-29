package team.illusion.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import team.illusion.ui.component.ConfirmButton
import team.illusion.ui.component.PasswordTextField
import team.illusion.ui.component.SettingItem
import team.illusion.ui.component.SettingToggle

val LocalMaxPasswordCount = compositionLocalOf { 10 }

@Composable
fun AdminScreen(uiState: AdminUiState, event: (AdminEvent) -> Unit) {
    val maxPasswordCount = LocalMaxPasswordCount.current
    var password by rememberSaveable(uiState.password) { mutableStateOf(uiState.password) }

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
            SettingItem("회원 등록") { event(AdminEvent.ClickMemberRegister) }
            SettingItem("회원 조회") { event(AdminEvent.ClickMemberSearch) }
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
        }
        ConfirmButton(text = "변경") { event(AdminEvent.ChangePassword(password)) }
    }
}

