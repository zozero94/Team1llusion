package team.illusion.ui.member.date

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class DateAttendanceViewModel @Inject constructor(
    memberRepository: MemberRepository,
) : ViewModel() {
    private val dateChangeEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val members = combine(dateChangeEvent, memberRepository.bindMembers()) { date, members ->
        members.filter { it.checkInDate.contains(date) }
    }


    fun changeDate(date:String) {
        dateChangeEvent.tryEmit(date)
    }
}
