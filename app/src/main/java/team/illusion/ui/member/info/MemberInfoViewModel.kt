package team.illusion.ui.member.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import team.illusion.data.DateManager
import team.illusion.data.model.Options
import team.illusion.data.model.Sex
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MemberInfoViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MemberInfoUiState(
            editMember = null,
            phoneVerify = true,
            canConfirm = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val id: String? = savedStateHandle[MemberInfoActivity.ID]
            if (id != null) {
                val member = memberRepository.getMember(id)
                _uiState.update { it.copy(editMember = member) }
            }
        }
    }

    fun register(
        name: String,
        phone: String,
        sex: Sex,
        option: Options,
        enableExtraOption: Boolean,
        address: String,
        comment: String,
        onCompletion: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        viewModelScope.launch {
            val verify = phone.matches(Regex("^010\\d{8}$"))

            val editMember = uiState.value.editMember
            val duplicateMember = memberRepository.findMember(editMember?.id, phone)
            when {
                duplicateMember != null && editMember == null -> {
                    onError("중복된 멤버가 있습니다.\n멤버 이름(${duplicateMember.name})")
                }
                verify -> {
                    if (editMember != null) {
                        memberRepository.editMember(
                            editMember.copy(
                                name = name,
                                phone = phone,
                                sex = sex,
                                option = option,
                                enableExtraOption = enableExtraOption,
                                address = address,
                                comment = comment,
                            )
                        )
                    } else {
                        memberRepository.registerMember(
                            name = name,
                            phone = phone,
                            sex = sex,
                            option = option,
                            enableExtraOption = enableExtraOption,
                            address = address,
                            comment = comment,
                            startDate = DateManager.today,
                            remainCount = if (enableExtraOption) {
                                option.count?.times(3)?.plus(option.extraCount)
                            } else {
                                option.count
                            }
                        )
                    }
                    onCompletion()
                }
                else -> {
                    _uiState.update { it.copy(phoneVerify = false) }
                    onError("휴대폰 번호는 010 xxxx xxxx 형태로 입력하세요.")
                }
            }
        }
    }

    suspend fun delete() {
        val id = requireNotNull(uiState.value.editMember?.id)
        memberRepository.deleteMember(id)
    }
}
