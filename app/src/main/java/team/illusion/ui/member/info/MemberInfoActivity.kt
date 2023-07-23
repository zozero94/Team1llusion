package team.illusion.ui.member.info

import android.app.DatePickerDialog
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
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import team.illusion.data.DateManager
import team.illusion.data.model.Member
import team.illusion.data.model.Options
import team.illusion.data.model.Sex
import team.illusion.ui.member.checkin.CheckInActivity
import team.illusion.ui.theme.Team1llusionTheme


@AndroidEntryPoint
class MemberInfoActivity : ComponentActivity() {

    private val viewModel by viewModels<MemberInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                MemberInfoScreen(
                    modifier = Modifier,
                    uiState = uiState
                ) { event ->
                    when (event) {
                        is MemberInfoEvent.Register -> {
                            viewModel.register(
                                name = event.name,
                                phone = event.phone,
                                sex = event.sex,
                                address = event.address,
                                option = event.selectedOption,
                                comment = event.comment,
                                onCompletion = {
                                    Toast.makeText(this@MemberInfoActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            ) { message ->
                                Toast.makeText(this@MemberInfoActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        MemberInfoEvent.Delete -> {
                            lifecycleScope.launch {
                                viewModel.delete()
                                finish()
                            }
                        }
                        MemberInfoEvent.OpenDatePicker -> {
                            DatePickerDialog(this)
                                .apply {
                                    setOnDateSetListener { _, year, month, dayOfMonth ->
                                        viewModel.setStartDate(DateManager.getFormattedDate(year, month, dayOfMonth))
                                    }
                                }.show()
                        }
                        is MemberInfoEvent.OpenCheckInState -> {
                            startActivity(CheckInActivity.getIntent(this,event.id))
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val ID = "id"
        fun getIntent(context: Context, id: String? = null) = Intent(context, MemberInfoActivity::class.java)
            .putExtra(ID, id)
    }

}


data class MemberInfoUiState(
    val editMember: Member?,
    val phoneVerify: Boolean,
    val canConfirm: Boolean,
    val startDate: String,
    val endDate:String
)

sealed interface MemberInfoEvent {
    data class Register(
        val name: String,
        val phone: String,
        val address: String,
        val comment: String,
        val sex: Sex,
        val selectedOption: Options
    ) : MemberInfoEvent

    data class OpenCheckInState(val id: String) : MemberInfoEvent

    object OpenDatePicker : MemberInfoEvent

    object Delete : MemberInfoEvent
}
