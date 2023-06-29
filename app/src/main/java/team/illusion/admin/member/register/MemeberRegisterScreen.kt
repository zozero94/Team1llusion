package team.illusion.admin.member.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import team.illusion.component.ConfirmButton
import team.illusion.component.NormalTextField
import team.illusion.component.SettingItem
import team.illusion.component.SettingRadio
import team.illusion.data.DateManager


@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun MemberRegisterScreen(
    modifier: Modifier, uiState: MemberRegisterUiState, event: (MemberRegisterEvent) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )


    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        sheetContent = {
            OptionBottomSheet(
                onClickConfirm = {
                    event(MemberRegisterEvent.ChangeOption(it))
                    scope.launch { sheetState.hide() }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "현재 날짜 : ${uiState.today}")
                    NormalTextField(text = uiState.name, label = "이름", keyboardType = KeyboardType.Text) {
                        event(MemberRegisterEvent.ChangeName(it))
                    }
                    NormalTextField(text = uiState.phone, label = "전화번호", keyboardType = KeyboardType.Phone) {
                        event(MemberRegisterEvent.ChangePhone(it))
                    }
                    SettingItem(text = "기간 설정 ${uiState.selectedOptions?.text.orEmpty()}") {
                        keyboard?.hide()
                        scope.launch { sheetState.show() }
                    }
                }
                ConfirmButton(
                    text = "확인",
                    isEnable = uiState.canRegister
                ) {
                    event(MemberRegisterEvent.Register)
                }
            }
        }
    )
}

@Composable
private fun OptionBottomSheet(onClickConfirm: (Options) -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var selected by remember { mutableStateOf<Options?>(null) }

        Options.values().forEach {
            SettingRadio(text = it.text, checked = selected == it) {
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
            today = DateManager.today,
            selectedOptions = null,
            name = "zero",
            phone = "01036108845",
            canRegister = false
        )
    ) {}
}