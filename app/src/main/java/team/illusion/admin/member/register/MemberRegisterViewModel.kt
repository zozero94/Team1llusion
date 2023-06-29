package team.illusion.admin.member.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import team.illusion.data.DateManager
import team.illusion.data.model.Member
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MemberRegisterViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MemberRegisterUiState(
            phoneVerify = true,
            canRegister = false
        )
    )
    val uiState = _uiState.asStateFlow()

    fun register(
        member: Member,
        onCompletion: () -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        viewModelScope.launch {
            val member = member.copy(
                endDate = DateManager.calculateDateAfterMonths(
                    target = DateManager.today,
                    months = if (member.enableExtraOption) 3 else 1
                ),
                remainCount = member.option.count + if (member.enableExtraOption) member.option.extraCount else 0,
            )
            val verify = member.phone.matches(Regex("^010\\d{8}$"))

            val duplicateMember = memberRepository.findMember(member)
            when {
                duplicateMember != null -> {
                    onError("중복된 멤버가 있습니다.\n멤버 이름(${duplicateMember.name})")
                }
                verify -> {
                    memberRepository.registerMember(member)
                    onCompletion()
                }
                else -> {
                    _uiState.update { it.copy(phoneVerify = false) }
                    onError("휴대폰 번호는 010 xxxx xxxx 형태로 입력하세요.")
                }
            }
        }
    }
}
