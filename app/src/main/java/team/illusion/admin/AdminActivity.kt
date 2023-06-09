package team.illusion.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class AdminActivity : ComponentActivity() {

    private val adminViewModel by viewModels<AdminViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val uiState = adminViewModel.uiState.collectAsStateWithLifecycle().value
                val lockUiState = adminViewModel.lockUiState.collectAsStateWithLifecycle().value

                if (uiState != null && lockUiState != null) {
                    if (lockUiState.isLock) {
                        LockScreen(isFail = lockUiState.isFail, unLock = adminViewModel::unLock)
                    } else {
                        CompositionLocalProvider(values = arrayOf(LocalMaxPasswordCount provides 10)) {
                            AdminScreen(uiState) { adminEvent ->
                                when (adminEvent) {
                                    is AdminEvent.TogglePassword -> {
                                        lifecycleScope.launch {
                                            adminViewModel.changePasswordState(adminEvent.isEnable)
                                        }
                                    }
                                    is AdminEvent.OverPasswordLimit -> {
                                        Toast.makeText(
                                            this,
                                            String.format("%d자를 넘지마셈", adminEvent.limit),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    is AdminEvent.ChangePassword -> {
                                        lifecycleScope.launch {
                                            adminViewModel.changePassword(adminEvent.password)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }


    companion object {
        fun getIntent(context: Context) = Intent(context, AdminActivity::class.java)
    }

}

sealed interface AdminEvent {
    data class TogglePassword(val isEnable: Boolean) : AdminEvent
    data class OverPasswordLimit(val limit: Int) : AdminEvent

    data class ChangePassword(val password: String) : AdminEvent
}

data class AdminUiState(
    val usePassword: Boolean,
    val password: String = "",


    )

data class LockUiState(
    val isLock: Boolean,
    val isFail: Boolean
)