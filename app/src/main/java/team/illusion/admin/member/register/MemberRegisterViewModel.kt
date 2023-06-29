package team.illusion.admin.member.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
            today = DateManager.today,
            selectedOptions = null,
            name = "",
            phone = "",
            phoneVerify = true,
            canRegister = false
        )
    )

    val uiState = _uiState.asStateFlow()

    init {
        _uiState
            .onEach {
                _uiState.update {
                    it.copy(
                        canRegister = it.name.isNotEmpty() && it.phone.isNotEmpty() && it.selectedOptions != null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun changeName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun changePhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun changeOption(options: Options) {
        _uiState.update { it.copy(selectedOptions = options) }
    }

    fun register(onCompletion: () -> Unit, onError: (errorMessage: String) -> Unit) {
        viewModelScope.launch {
            val member = with(uiState.value) {
                Member(name = name, phone = phone, option = requireNotNull(selectedOptions?.text))
            }
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
