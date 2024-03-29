package team.illusion.ui.admin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.illusion.data.model.Center
import team.illusion.ui.member.date.DateAttendanceActivity
import team.illusion.ui.member.info.MemberInfoActivity
import team.illusion.ui.member.search.MemberSearchActivity
import team.illusion.ui.theme.Team1llusionTheme
import team.illusion.util.restartIntent
import team.illusion.util.showToast



@AndroidEntryPoint
class AdminActivity : ComponentActivity() {

    private val adminViewModel by viewModels<AdminViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindAuthorizeEvents()
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
                                        adminViewModel.changePasswordState(adminEvent.isEnable)
                                    }
                                    is AdminEvent.OverPasswordLimit -> {
                                        showToast(String.format("%d자를 넘지마셈", adminEvent.limit))
                                    }
                                    is AdminEvent.ChangePassword -> {
                                        lifecycleScope.launch {
                                            adminViewModel.changePassword(adminEvent.password)
                                            finish()
                                        }
                                    }
                                    AdminEvent.ClickMemberRegister -> {
                                        startActivity(MemberInfoActivity.getIntent(this))
                                    }

                                    AdminEvent.ClickMemberSearch -> {
                                        startActivity(MemberSearchActivity.getIntent(this))
                                    }

                                    is AdminEvent.Delete -> {
                                        lifecycleScope.launch {
                                            when(adminEvent.deleteOptions){
                                                DeleteOptions.ALL -> adminViewModel.deleteAll()
                                                DeleteOptions.EXPIRE -> adminViewModel.deleteExpire()
                                            }
                                            startActivity(restartIntent(this@AdminActivity))
                                        }
                                    }

                                    AdminEvent.DateAttendance -> {
                                        startActivity(DateAttendanceActivity.getIntent(this))
                                    }

                                    AdminEvent.OpenCenterDialog -> {
                                        AlertDialog
                                            .Builder(this)
                                            .setItems(
                                                Center.values().map { it.centerName }
                                                    .toTypedArray()
                                            ) { _, choice ->
                                                val selectedCenter = Center.values()[choice]
                                                adminViewModel.updateCenter(selectedCenter)
                                            }
                                            .show()
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

    override fun onResume() {
        super.onResume()
        adminViewModel.refresh()
    }

    private fun bindAuthorizeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                adminViewModel.event.collectLatest { event ->
                    when (event) {
                        is AuthorizeEvent.AccessDenied -> {
                            showToast(event.throwable.message)
                        }
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
    object ClickMemberRegister : AdminEvent
    object ClickMemberSearch : AdminEvent
    data class Delete(val deleteOptions: DeleteOptions) : AdminEvent
    object DateAttendance : AdminEvent

    data class TogglePassword(val isEnable: Boolean) : AdminEvent
    data class OverPasswordLimit(val limit: Int) : AdminEvent

    data class ChangePassword(val password: String) : AdminEvent
    object OpenCenterDialog : AdminEvent

}

sealed interface AuthorizeEvent {
    data class AccessDenied(val throwable: Throwable) : AuthorizeEvent
}

data class AdminUiState(
    val center: Center,
    val usePassword: Boolean,
    val password: String = "",
    val currentAdminEmail: String,
)

data class LockUiState(
    val isLock: Boolean,
    val isFail: Boolean,
)
