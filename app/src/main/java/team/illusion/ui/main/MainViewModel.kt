package team.illusion.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import team.illusion.data.datasource.CenterStore
import team.illusion.data.datasource.GoogleManager
import team.illusion.data.model.Center
import team.illusion.data.model.Member
import team.illusion.data.repository.AdminRepository
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val memberRepository: MemberRepository,
    private val googleManager: GoogleManager,
    centerStore: CenterStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainUiState(
            center = Center.None,
            memberIdentifier = "",
            members = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _verifyEvent = MutableSharedFlow<VerifyEvent>(extraBufferCapacity = 1)
    val verifyEvent = _verifyEvent.asSharedFlow()

    val signInIntent = googleManager.client.signInIntent

    init {
        viewModelScope.launch {
            centerStore.center.collectLatest { center ->
                _uiState.update { it.copy(center = Center.findCenter(center)) }
            }
        }
    }

    fun updateId(id: String) {
        _uiState.update {
            val identifier = if (id.isEmpty()) "" else it.memberIdentifier + id
            it.copy(memberIdentifier = identifier)
        }
    }

    fun deleteId() {
        _uiState.update {
            it.copy(memberIdentifier = it.memberIdentifier.dropLast(1))
        }
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
        }
    }

    suspend fun checkIn(member: Member) {
        runCatching { memberRepository.checkIn(member) }
            .onSuccess {
                _verifyEvent.emit(VerifyEvent.CheckIn(name = member.name, remainCount = member.remainCount))
            }
            .onFailure {
                _verifyEvent.emit(VerifyEvent.Error(it))
            }
    }

    fun logout() {
        viewModelScope.launch {
            googleManager.logout()
            _verifyEvent.emit(VerifyEvent.Logout)
        }
    }

    suspend fun login(intent: Intent?): Boolean {
        val allows = adminRepository.getAllowAdmins().orEmpty()
        val user = googleManager.signIn(intent)
        val isVerified = allows.contains(user?.email.orEmpty())
        if (!isVerified) logout()
        return isVerified
    }

}
