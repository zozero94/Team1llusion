package team.illusion.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import team.illusion.component.ConfirmButton
import team.illusion.component.PasswordTextField

@Composable
fun LockScreen(isFail: Boolean, unLock: (password: String) -> Unit) {
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PasswordTextField(
            password = password,
            isFail = isFail,
            onValueChange = { password = it }
        ) { password = "" }
        ConfirmButton(text = "확인") {
            unLock(password)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    LockScreen(isFail = true) {}
}