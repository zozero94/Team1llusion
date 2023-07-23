package team.illusion.ui.member.info

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import team.illusion.MemberPreviewProvider
import team.illusion.data.DateManager
import team.illusion.data.model.Count
import team.illusion.data.model.Member
import team.illusion.data.model.Sex
import team.illusion.ui.component.*


@Composable
fun MemberInfoScreen(
    uiState: MemberInfoUiState,
    event: (MemberInfoEvent) -> Unit,
) {
    var name by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.name.orEmpty()) }
    var phone by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.phone.orEmpty()) }
    var address by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.address.orEmpty()) }
    var comment by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.comment.orEmpty()) }
    var sex by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.sex ?: Sex.Male) }
    var count by rememberSaveable(uiState.editMember) {
        mutableStateOf(uiState.editMember?.remainCount?.count?.toString().orEmpty())
    }

    var openDialog by rememberSaveable { mutableStateOf(false) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            backgroundColor = Color.White,
            confirmButton = {
                TextButton(onClick = { event(MemberInfoEvent.Delete) }) {
                    Text(text = stringResource(id = android.R.string.ok), color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text(text = stringResource(id = android.R.string.cancel), color = Color.Black)
                }
            },
            text = { Text(text = "삭제하시겠습니까?", color = Color.Black) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "현재 날짜 : ${DateManager.today}")
            NormalTextField(text = name, label = "이름", keyboardType = KeyboardType.Text) {
                name = it
            }
            NormalTextField(
                text = phone,
                label = "전화번호",
                keyboardType = KeyboardType.Phone,
                isError = !uiState.phoneVerify
            ) {
                phone = it
            }
            NormalTextField(
                text = address,
                label = "주소",
                keyboardType = KeyboardType.Text
            ) {
                address = it
            }
            NormalTextField(
                text = comment,
                label = "기타",
                keyboardType = KeyboardType.Text
            ) {
                comment = it
            }

            SexRadio(sex = sex, onClick = { sex = it })

            NormalTextField(
                text = count,
                label = "횟수 설정(공백시 무제한)",
                onValueChange = { count = it },
                keyboardType = KeyboardType.Number
            )
            SettingItem(text = "시작날짜 선택\n${uiState.startDate}") {
                val date = DateManager.parseLocalDate(uiState.startDate)
                event(MemberInfoEvent.OpenDatePicker(date.year, date.monthValue, date.dayOfMonth))
            }
            SettingItem(text = "종료날짜 선택\n${uiState.endDate}") {
                val date = DateManager.parseLocalDate(uiState.endDate)
                event(MemberInfoEvent.OpenDatePicker(date.year, date.monthValue, date.dayOfMonth))
            }
            if (uiState.editMember != null) {
                SettingItem(text = "checkIn 확인하기") {
                    event(MemberInfoEvent.OpenCheckInState(uiState.editMember.id))
                }
            }
        }
        Column {
            if (uiState.editMember != null) {
                ConfirmButton(
                    text = "삭제",
                    buttonColors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.Red
                    )
                ) {
                    openDialog = true
                }
            }
            ConfirmButton(
                text = "확인",
                isEnable = name.isNotEmpty() && phone.isNotEmpty()
            ) {
                event(
                    MemberInfoEvent.Register(
                        name = name,
                        phone = phone,
                        address = address,
                        comment = comment,
                        sex = sex,
                        count = Count(count.toIntOrNull())
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(MemberPreviewProvider::class) member: Member
) {
    MemberInfoScreen(
        uiState = MemberInfoUiState(
            editMember = member,
            phoneVerify = false,
            canConfirm = false,
            startDate = "",
            endDate = ""
        )
    ) {}
}
