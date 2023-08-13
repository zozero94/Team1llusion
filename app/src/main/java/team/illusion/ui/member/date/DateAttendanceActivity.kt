package team.illusion.ui.member.date

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import team.illusion.data.DateManager
import team.illusion.ui.component.MemberColumn
import team.illusion.ui.member.info.MemberInfoActivity
import team.illusion.ui.theme.Team1llusionTheme

@AndroidEntryPoint
class DateAttendanceActivity : ComponentActivity() {

    private val viewModel by viewModels<DateAttendanceViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team1llusionTheme {
                val members by viewModel.members.collectAsStateWithLifecycle(initialValue = emptyList())
                var selectedDate by rememberSaveable { mutableStateOf(DateManager.today) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = {
                            CalendarView(this@DateAttendanceActivity).apply {
                                setOnDateChangeListener { _, year, month, dayOfMonth ->
                                    selectedDate = DateManager.getFormattedDate(year, month, dayOfMonth)
                                    viewModel.changeDate(selectedDate)
                                }
                            }
                        }
                    )
                    Column {
                        Text(text = "오늘 : ${DateManager.today}")
                        Text(text = "선택 : $selectedDate")
                    }
                    MemberColumn(
                        members = members,
                        clickMember = {
                            startActivity(
                                MemberInfoActivity.getIntent(
                                    this@DateAttendanceActivity,
                                    it.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, DateAttendanceActivity::class.java)
    }
}
