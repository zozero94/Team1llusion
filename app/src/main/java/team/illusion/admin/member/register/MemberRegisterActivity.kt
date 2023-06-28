package team.illusion.admin.member.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import team.illusion.ui.theme.Team1llusionTheme

enum class Options(
    val text: String,
    val description: String? = null,
) {
    Option1("1"),
    Option2("2"),
    Option3("3"),
    Option4("4"),
}

@AndroidEntryPoint
class MemberRegisterActivity : ComponentActivity() {

    private val viewModel by viewModels<MemberRegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MemberRegisterScreen(uiState) { event ->
                    when (event) {
                        is MemberRegisterEvent.ChangeName -> viewModel.changeName(event.name)
                        is MemberRegisterEvent.ChangePhone -> viewModel.changePhone(event.phone)
                        is MemberRegisterEvent.ChangeOption -> viewModel.changeOption(event.options)
                        MemberRegisterEvent.Register -> {
                            lifecycleScope.launch {
                                viewModel.register()
                                Toast.makeText(this@MemberRegisterActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
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
    val today: String,
    val selectedOptions: Options?,
    val name: String,
    val phone: String,
    val canRegister: Boolean
)

sealed interface MemberRegisterEvent {
    object Register : MemberRegisterEvent
    data class ChangeName(val name: String) : MemberRegisterEvent
    data class ChangePhone(val phone: String) : MemberRegisterEvent
    data class ChangeOption(val options: Options): MemberRegisterEvent
}