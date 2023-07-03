package team.illusion.ui.member.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import team.illusion.MembersPreviewProvider
import team.illusion.data.model.Member
import team.illusion.ui.component.MemberColumn
import team.illusion.ui.component.SearchTextField

@Composable
fun MemberSearchScreen(
    uiState: MemberSearchUiState,
    query: String,
    queryChanged: (String) -> Unit,
    selectMember: (Member) -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SearchTextField(
            query = query,
            onValueChange = queryChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
        MemberColumn(
            members = if (query.isEmpty()) uiState.members else uiState.searched,
            clickMember = selectMember
        )
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MembersPreviewProvider::class) members: List<Member>) {
    MemberSearchScreen(
        uiState = MemberSearchUiState(
            members = members,
            searched = emptyList()
        ),
        query = "",
        queryChanged = {},
        selectMember = {}
    )
}