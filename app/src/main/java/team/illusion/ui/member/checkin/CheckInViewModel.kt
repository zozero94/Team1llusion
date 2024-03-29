package team.illusion.ui.member.checkin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val member = memberRepository.bindMember(requireNotNull(savedStateHandle[CheckInActivity.ID]))
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun deleteCheckIn(checkInDate: String) {
        viewModelScope.launch {
            val member = requireNotNull(member.value)
            memberRepository.editMember(
                member.copy(
                    checkInDate = member.checkInDate - checkInDate,
                    remainCount = member.remainCount + 1
                )
            )
        }
    }
}
