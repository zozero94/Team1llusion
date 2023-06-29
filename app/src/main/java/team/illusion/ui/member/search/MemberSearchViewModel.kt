package team.illusion.ui.member.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import team.illusion.data.repository.MemberRepository
import javax.inject.Inject

@HiltViewModel
class MemberSearchViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MemberSearchUiState(
            members = emptyList(),
            searched = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()


    init {
        viewModelScope.launch {
            memberRepository.bindMembers().collectLatest { members ->
                _uiState.update { it.copy(members = members) }
            }
        }
        viewModelScope.launch {

            _query.debounce(300)
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
        _query.update { query }
    }

}
