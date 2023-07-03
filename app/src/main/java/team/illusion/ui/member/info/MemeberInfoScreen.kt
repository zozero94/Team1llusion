package team.illusion.ui.member.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import team.illusion.MemberPreviewProvider
import team.illusion.R
import team.illusion.data.DateManager
import team.illusion.data.model.Member
import team.illusion.data.model.Options
import team.illusion.data.model.Sex
import team.illusion.ui.component.*


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MemberInfoScreen(
    modifier: Modifier,
    uiState: MemberInfoUiState,
    event: (MemberInfoEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var selectedOption by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.option) }
    var enableExtraOption by rememberSaveable(uiState.editMember) {
        mutableStateOf(uiState.editMember?.enableExtraOption ?: false)
    }

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        sheetContent = {
            OptionBottomSheet(
                enableExtraOption = enableExtraOption,
                onClickConfirm = {
                    selectedOption = it
                    scope.launch { sheetState.hide() }
                },
                onClickExtraOption = {
                    enableExtraOption = it
                }
            )
        },
        content = {
            MemberInfoScreen(
                uiState = uiState,
                selectedOption = selectedOption,
                enableExtraOption = enableExtraOption,
                event = event,
                sheetState = sheetState
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun MemberInfoScreen(
    uiState: MemberInfoUiState,
    selectedOption: Options?,
    enableExtraOption: Boolean,
    event: (MemberInfoEvent) -> Unit,
    sheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    var name by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.name.orEmpty()) }
    var phone by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.phone.orEmpty()) }
    var address by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.address.orEmpty()) }
    var comment by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.comment.orEmpty()) }
    var sex by rememberSaveable(uiState.editMember) { mutableStateOf(uiState.editMember?.sex ?: Sex.Male) }

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
            SettingItem(Modifier, text = "기간 설정 ${selectedOption?.name.orEmpty()}") {
                keyboard?.hide()
                scope.launch { sheetState.show() }
            }
            SettingItem(text = "시작날짜 선택\n${uiState.startDate}") {
                event(MemberInfoEvent.OpenDatePicker)
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
                isEnable = name.isNotEmpty() && phone.isNotEmpty() && selectedOption != null
            ) {
                event(
                    MemberInfoEvent.Register(
                        name = name,
                        phone = phone,
                        address = address,
                        comment = comment,
                        sex = sex,
                        enableExtraOption = enableExtraOption,
                        selectedOption = requireNotNull(selectedOption)
                    )
                )
            }
        }
    }
}

@Composable
private fun OptionBottomSheet(
    enableExtraOption: Boolean,
    onClickConfirm: (Options) -> Unit,
    onClickExtraOption: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var selected by rememberSaveable { mutableStateOf<Options?>(null) }
        Image(painter = painterResource(id = R.mipmap.account), contentDescription = null)
        ExtraOptionRadio(enableExtraOption,onClickExtraOption)
        Options.values().forEach {
            SettingRadio(text = it.name, checked = selected == it) {
                selected = it
            }
        }
        ConfirmButton(text = "확인") {
            selected?.let { onClickConfirm(it) }
        }
    }
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(MemberPreviewProvider::class) member: Member
) {
    MemberInfoScreen(
        modifier = Modifier,
        uiState = MemberInfoUiState(
            editMember = member,
            phoneVerify = false,
            canConfirm = false,
            startDate = ""
        )
    ) {}
}