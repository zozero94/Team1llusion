package team.illusion.admin.member.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.data.model.Member
import team.illusion.data.model.Options
import team.illusion.data.model.Sex
import team.illusion.ui.theme.Team1llusionTheme


@AndroidEntryPoint
class MemberRegisterActivity : ComponentActivity() {

    private val viewModel by viewModels<MemberRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MemberRegisterScreen(
                    modifier = Modifier,
                    uiState = uiState
                ) { event ->
                    when (event) {
                        is MemberRegisterEvent.Register -> {
                            viewModel.register(
                                member = with(event) {
                                    Member(
                                        name = name,
                                        phone = phone,
                                        sex = sex,
                                        address = address,
                                        option = selectedOption,
                                        enableExtraOption = enableExtraOption,
                                        comment = comment
                                    )
                                },
                                onCompletion = {
                                    Toast.makeText(this@MemberRegisterActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                },
                                onError = { message ->
                                    Toast.makeText(this@MemberRegisterActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MemberRegisterActivity::class.java)
    }

}


data class MemberRegisterUiState(
    val phoneVerify: Boolean,
    val canRegister: Boolean
)

sealed interface MemberRegisterEvent {
    data class Register(
        val name: String,
        val phone: String,
        val address: String,
        val comment: String,
        val sex: Sex,
        val enableExtraOption: Boolean,
        val selectedOption: Options
    ) : MemberRegisterEvent
}