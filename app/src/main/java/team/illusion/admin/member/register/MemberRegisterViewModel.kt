package team.illusion.admin.member.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    suspend fun register() {
        val member = with(uiState.value) {
            Member(name, phone, requireNotNull(selectedOptions?.text))
        }
        memberRepository.registerMember(member)
    }
}
