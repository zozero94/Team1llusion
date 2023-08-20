package team.illusion.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import team.illusion.data.model.Member
import team.illusion.data.model.Sex
import team.illusion.data.model.isExpireDate

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
fun ExtraOptionRadio(enableExtraOption: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "1달")
            RadioButton(selected = !enableExtraOption, onClick = { onClick(false) })
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "3달")
            RadioButton(selected = enableExtraOption, onClick = { onClick(true) })
        }
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
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
) {

    TextField(
        modifier = modifier,
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

const val REMAIN_COUNT = 3

interface Filterable<T> {
    fun filter(value: T): Boolean
}

enum class Filter(val desc: String) : Filterable<Member> {
    Normal("일반") {
        override fun filter(value: Member): Boolean {
            return true
        }
    },
    RemainCount("${REMAIN_COUNT}회 미만") {
        override fun filter(value: Member): Boolean {
            val count = value.remainCount.count ?: return false
            return count < REMAIN_COUNT
        }
    },
    ExpireDate("기간만료") {
        override fun filter(value: Member): Boolean {
            return value.isExpireDate()
        }
    };
}

@Composable
fun SearchTextField(query: String, filter: Filter, onValueChange: (String, Filter) -> Unit) {
    var isOpen by rememberSaveable { mutableStateOf(false) }
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = { onValueChange(it, filter) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isOpen = !isOpen }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
                Text(text = filter.desc)
            }
            DropdownMenu(expanded = isOpen, onDismissRequest = { isOpen = false }) {
                Filter.values().forEach {
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(query, it)
                            isOpen = false
                        },
                        content = {
                            Text(text = it.desc)
                        }
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(
                enabled = query.isNotEmpty(),
                onClick = { onValueChange("", filter) }
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
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    isEnable: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.textButtonColors(backgroundColor = Color.LightGray),
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = buttonColors,
        shape = RoundedCornerShape(8.dp),
        enabled = isEnable
    ) {
        Text(text = text)
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.LightGray,
    click: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color, shape = RoundedCornerShape(8.dp))
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

@Composable
fun DeleteItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.Red)
    }
}
