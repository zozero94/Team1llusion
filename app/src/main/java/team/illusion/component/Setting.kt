package team.illusion.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingToggle(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun PasswordTextField(
    password: String,
    isFail: Boolean,
    onValueChange: (String) -> Unit,
    onClearPassword: () -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = password,
        onValueChange = onValueChange,
        label = { Text(text = "비밀번호") },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = onClearPassword) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(8.dp),
        maxLines = 1,
        singleLine = true,
        isError = isFail
    )
}

@Composable
fun ConfirmButton(text: String, onClick: () -> Unit) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(backgroundColor = Color.LightGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text)
    }
}