package team.illusion.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import team.illusion.data.datasource.CenterStore
import team.illusion.data.datasource.DateManager
import team.illusion.data.datasource.GoogleManager
import team.illusion.data.model.Center
import team.illusion.data.repository.AdminRepository
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val memberRepository: MemberRepository,
    private val googleManager: GoogleManager,
    private val centerStore: CenterStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _lockUiState = MutableStateFlow<LockUiState?>(null)
    val lockUiState = _lockUiState.asStateFlow()

    private val _event = MutableSharedFlow<AuthorizeEvent>()
    val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                adminRepository.bindUsePassword(),
                adminRepository.bindPassword(),
                centerStore.center
            ) { usePassword, password, center ->
                _uiState.update {
                    AdminUiState(
                        center = Center.findCenter(center),
                        usePassword = usePassword,
                        password = password,
                        currentAdminEmail = googleManager.getCurrentLoginUserEmail()
                    )
                }

                _lockUiState.update {
                    LockUiState(isLock = usePassword && password.isNotEmpty(), isFail = false)
                }
            }.collect()
        }
    }

    fun refresh() {
        _uiState.update {
            it?.copy(currentAdminEmail = googleManager.getCurrentLoginUserEmail())
        }
    }

    suspend fun changePassword(password: String) {
        runCatching {
            val usePassword = uiState.value?.usePassword ?: false
            adminRepository.updateUsePassword(usePassword)
            if (!usePassword) {
                adminRepository.changePassword("")
            } else {
                adminRepository.changePassword(password)
            }
        }
            .onFailure {
                _event.emit(AuthorizeEvent.AccessDenied(it))
            }
    }

    fun changePasswordState(usePassword: Boolean) {
        _uiState.update {
            it?.copy(password = "", usePassword = usePassword)
        }
    }

    fun unLock(password: String) {
        _lockUiState.update {
            val isVerify = uiState.value?.password == password
            it?.copy(isLock = !isVerify, isFail = !isVerify)
        }
    }

    suspend fun deleteAll() {
        adminRepository.deleteAll()
        memberRepository.deleteAll()
    }

    suspend fun deleteExpire() {
        val members = memberRepository.getMembers()
        val expireMembers = members.filter { DateManager.isExpire(it.endDate) || it.remainCount.isExpire() }
        expireMembers.forEach {
            memberRepository.deleteMember(it.id)
        }
    }

    fun updateCenter(selectedCenter: Center) {
        viewModelScope.launch {
            centerStore.updateCenter(selectedCenter)
        }
    }

}

