package team.illusion.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import team.illusion.data.AdminRepository
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _lockUiState = MutableStateFlow<LockUiState?>(null)
    val lockUiState = _lockUiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(adminRepository.bindUsePassword(), adminRepository.bindPassword()) { usePassword, password ->
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
        val usePassword = uiState.value?.usePassword ?: false
        adminRepository.updateUsePassword(usePassword)
        if (!usePassword) {
            adminRepository.changePassword("")
        } else {
            adminRepository.changePassword(password)
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
}

