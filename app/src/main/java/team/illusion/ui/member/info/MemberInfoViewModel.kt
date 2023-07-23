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
import team.illusion.data.model.Count
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
            canConfirm = false,
            startDate = DateManager.today,
            endDate = DateManager.calculateDateAfterMonths(DateManager.today, 1)
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val id: String? = savedStateHandle[MemberInfoActivity.ID]
            if (id != null) {
                val member = memberRepository.getMember(id)
                _uiState.update {
                    it.copy(
                        editMember = member,
                        startDate = member?.startDate ?: uiState.value.startDate,
                        endDate = member?.endDate ?: uiState.value.endDate
                    )
                }
            }
        }
    }

    fun register(
        name: String,
        phone: String,
        sex: Sex,
        count: Count,
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
                                remainCount = count,
                                address = address,
                                comment = comment,
                                startDate = uiState.value.startDate,
                                endDate = uiState.value.endDate
                            )
                        )
                    } else {
                        memberRepository.registerMember(
                            name = name,
                            phone = phone,
                            sex = sex,
                            address = address,
                            comment = comment,
                            startDate = uiState.value.startDate,
                            endDate = DateManager.calculateDateAfterMonths(
                                target = uiState.value.startDate,
                                months = 1
                            ),
                            remainCount = count,
                            checkInDate = emptyList()
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

    fun settDate(isStart: Boolean, date: String) {
        _uiState.update {
            if (isStart)
                it.copy(startDate = date, endDate = DateManager.calculateDateAfterMonths(date, 1))
            else
                it.copy(endDate = date)
        }
    }
}
