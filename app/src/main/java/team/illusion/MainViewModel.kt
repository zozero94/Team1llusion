package team.illusion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import team.illusion.data.model.Member
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainUiState(
            memberIdentifier = "",
            members = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _verifyEvent = MutableSharedFlow<VerifyEvent>(extraBufferCapacity = 1)
    val verifyEvent = _verifyEvent.asSharedFlow()

    fun updateId(id: String) {
        _uiState.update { it.copy(memberIdentifier = id) }
    }

    fun verify() {
        viewModelScope.launch {
            runCatching {
                val members = memberRepository.getMemberByLastPhoneNumber(uiState.value.memberIdentifier)
                when (members.size) {
                    0 -> {
                        _verifyEvent.emit(VerifyEvent.Error)
                    }
                    1 -> {
                        _verifyEvent.emit(VerifyEvent.Confirm)
                        _uiState.update { it.copy(members = emptyList(), memberIdentifier = "") }
                    }
                    else -> {
                        _verifyEvent.emit(VerifyEvent.Duplicate)
                        _uiState.update { it.copy(members = members) }
                        //todo open bottomSheet

                    }
                }
            }

        }
    }

    fun checkIn(member: Member) {
        memberRepository.checkIn(member)
    }
}