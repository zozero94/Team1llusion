package team.illusion.admin.member.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import team.illusion.R
import team.illusion.component.*
import team.illusion.data.DateManager
import team.illusion.data.model.Options
import team.illusion.data.model.Sex


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MemberRegisterScreen(
    modifier: Modifier,
    uiState: MemberRegisterUiState,
    event: (MemberRegisterEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var selectedOption by rememberSaveable { mutableStateOf<Options?>(null) }

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        sheetContent = {
            OptionBottomSheet(
                onClickConfirm = {
                    selectedOption = it
                    scope.launch { sheetState.hide() }
                }
            )
        },
        content = {
            MemberRegisterScreen(
                uiState = uiState,
                selectedOption = selectedOption,
                event = event,
                sheetState = sheetState
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun MemberRegisterScreen(
    uiState: MemberRegisterUiState,
    selectedOption: Options?,
    event: (MemberRegisterEvent) -> Unit,
    sheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf(Sex.Male) }
    var enableExtraCount by rememberSaveable { mutableStateOf(false) }

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
            SettingItem(text = "기간 설정 ${selectedOption?.name.orEmpty()}") {
                keyboard?.hide()
                scope.launch { sheetState.show() }
            }
            SettingRadio(text = "추가 횟수", checked = enableExtraCount) {
                enableExtraCount = !enableExtraCount
            }
        }
        ConfirmButton(
            text = "확인",
            isEnable = name.isNotEmpty() && phone.isNotEmpty() && selectedOption != null
        ) {
            event(
                MemberRegisterEvent.Register(
                    name = name,
                    phone = phone,
                    address = address,
                    comment = comment,
                    sex = sex,
                    enableExtraOption = enableExtraCount,
                    selectedOption = requireNotNull(selectedOption)
                )
            )
        }
    }
}

@Composable
private fun OptionBottomSheet(onClickConfirm: (Options) -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var selected by rememberSaveable { mutableStateOf<Options?>(null) }
        Image(painter = painterResource(id = R.mipmap.account), contentDescription = null)
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
private fun Preview() {
    MemberRegisterScreen(
        modifier = Modifier,
        uiState = MemberRegisterUiState(
            phoneVerify = false,
            canRegister = false
        )
    ) {}
}