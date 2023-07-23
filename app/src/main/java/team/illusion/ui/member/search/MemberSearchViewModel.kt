package team.illusion.ui.member.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import team.illusion.data.model.Member
import team.illusion.data.repository.MemberRepository
import team.illusion.ui.component.Filter
import javax.inject.Inject

@HiltViewModel
class MemberSearchViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {
    private val _member = MutableStateFlow(emptyList<Member>())
    val members = _member.asStateFlow()
    private var _allMembers = MutableStateFlow(emptyList<Member>())

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _filter = MutableStateFlow(Filter.Normal)
    val filter = _filter.asStateFlow()

    init {
        viewModelScope.launch {
            memberRepository.bindMembers().collectLatest { members ->
                _allMembers.update { members }
            }
        }
        viewModelScope.launch {
            combine(
                flow = _query.debounce(300),
                flow2 = filter,
                flow3 = _allMembers
            ) { query, filter, allMember ->
                _member.update {
                    allMember.filter { member ->
                        val textFilter =
                            member.name.contains(query) || member.phone.contains(query)
                        val optionFilter = filter.filter(member)
                        if (query.isEmpty()) optionFilter else optionFilter && textFilter
                    }
                }
            }.collect()
        }
    }

    fun query(query: String, filter: Filter) {
        _query.update { query }
        _filter.update { filter }
    }

}
