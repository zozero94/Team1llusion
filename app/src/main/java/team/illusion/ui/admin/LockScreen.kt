package team.illusion.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.illusion.ui.component.ConfirmButton
import team.illusion.ui.component.PasswordTextField

@Composable
fun LockScreen(isFail: Boolean, unLock: (password: String) -> Unit) {
    var password by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "암호 잠금 상태",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
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