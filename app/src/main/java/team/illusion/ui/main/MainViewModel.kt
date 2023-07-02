package team.illusion.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import team.illusion.data.model.Member
import team.illusion.data.model.isExpireDate
import team.illusion.data.repository.AdminRepository
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    adminRepository: AdminRepository,
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

    init {
        viewModelScope.launch {
            adminRepository.initialize()
        }
    }

    fun updateId(id: String) {
        _uiState.update { it.copy(memberIdentifier = id) }
    }

    fun verify() {
        viewModelScope.launch {
            runCatching {
                require(uiState.value.memberIdentifier.isNotEmpty()) { "입력값이 없습니다." }
                val members = memberRepository.getMembers(uiState.value.memberIdentifier).first()
                when (members.size) {
                    0 -> {
                        _verifyEvent.emit(VerifyEvent.Empty)
                    }
                    1 -> {
                        _verifyEvent.emit(VerifyEvent.Confirm(members.first()))
                        _uiState.update { it.copy(members = emptyList()) }
                    }
                    else -> {
                        _verifyEvent.emit(VerifyEvent.Duplicate)
                        _uiState.update { it.copy(members = members) }
                    }
                }
            }.onFailure {
                _verifyEvent.emit(VerifyEvent.Error(it))
            }
            updateId("")

        }
    }

    suspend fun checkIn(member: Member) {
        runCatching {
            val remainCount = member.remainCount
            if (remainCount != null) {
                require(remainCount > 0) { "회원권이 모두 소진 되었습니다." }
            }
            require(!member.isExpireDate()) { "기간이 만료 되었습니다." }
            memberRepository.checkIn(member)
        }
            .onSuccess {
                _verifyEvent.emit(VerifyEvent.CheckIn(name = member.name, remainCount = member.remainCount))
            }
            .onFailure {
                _verifyEvent.emit(VerifyEvent.Error(it))
            }
    }
}