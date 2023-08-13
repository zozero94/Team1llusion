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
import team.illusion.data.repository.AdminRepository
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val memberRepository: MemberRepository,
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
                adminRepository.bindPassword()
            ) { usePassword, password ->
                _uiState.update {
                    AdminUiState(
                        usePassword = usePassword,
                        password = password,
                    )
                }

                _lockUiState.update {
                    LockUiState(isLock = usePassword && password.isNotEmpty(), isFail = false)
                }
            }.collect()
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

}

