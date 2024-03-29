package team.illusion.ui.member.date

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import team.illusion.data.datasource.DateManager
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class DateAttendanceViewModel @Inject constructor(
    memberRepository: MemberRepository,
) : ViewModel() {
    private val dateChangeEvent = MutableStateFlow(DateManager.today)

    val members = combine(dateChangeEvent, memberRepository.bindMembers()) { date, members ->
        members.filter { member ->
            member.checkInDate.any { checkIn -> checkIn.contains(date) }
        }
    }


    fun changeDate(date:String) {
        dateChangeEvent.tryEmit(date)
    }
}
