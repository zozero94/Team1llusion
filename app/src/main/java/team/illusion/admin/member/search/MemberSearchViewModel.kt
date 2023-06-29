package team.illusion.admin.member.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import team.illusion.data.model.Member
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MemberSearchViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MemberSearchUiState(
            query = "",
            members = emptyList(),
            searched = emptyList()
        )
    )

    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            memberRepository.bindMembers().collectLatest { members ->
                _uiState.update { it.copy(members = members) }
            }
        }
        viewModelScope.launch {


            _uiState
                .map { it.query }
                .debounce(300)
                .collectLatest { query ->
                    _uiState.update {
                        if (query.isEmpty()) {
                            it.copy(searched = emptyList())
                        } else {
                            it.copy(
                                searched = it.members.filter { member ->
                                    member.name.contains(query) || member.phone.contains(query)
                                }
                            )
                        }
                    }
                }
        }
    }

    fun query(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun editMember(member: Member) {
        viewModelScope.launch {
            //todo remove 할때 prevMember를 넘겨줘야함, Screen Composable 재사용 구조에대해서 다시 고민해보기
            memberRepository.removeMember(member)
            memberRepository.registerMember(member)
        }

    }

}
