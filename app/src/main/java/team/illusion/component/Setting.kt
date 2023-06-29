package team.illusion.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import team.illusion.data.model.Sex

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
fun SettingRadio(text: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
        RadioButton(selected = checked, onClick = onClick)
    }
}

@Composable
fun SexRadio(sex: Sex, onClick: (Sex) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "성별", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sex.values().forEach {
                Text(text = it.name)
                RadioButton(selected = it == sex, onClick = { onClick(it) })
            }
        }
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
fun NormalTextField(
    text: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    onValueChange: (String) -> Unit
) {

    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        },
        shape = RoundedCornerShape(8.dp),
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
    )
}

@Composable
fun SearchTextField(query: String, onValueChange: (String) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(
                enabled = query.isNotEmpty(),
                onClick = { onValueChange("") }
            ) {
                Icon(
                    imageVector = if (query.isEmpty()) Icons.Default.Search else Icons.Default.Close,
                    contentDescription = null
                )
            }
        },
        shape = RoundedCornerShape(8.dp),
        maxLines = 1,
        singleLine = true,
    )
}

@Composable
fun ConfirmButton(
    text: String,
    isEnable: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(backgroundColor = Color.LightGray),
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = buttonColors,
        shape = RoundedCornerShape(8.dp),
        enabled = isEnable
    ) {
        Text(text = text)
    }
}

@Composable
fun SettingItem(text: String, click: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = click)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
        IconButton(onClick = click) {
            Image(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}
