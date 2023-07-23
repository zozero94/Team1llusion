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
import team.illusion.ui.component.Filter
import team.illusion.ui.component.MemberColumn
import team.illusion.ui.component.SearchTextField

@Composable
fun MemberSearchScreen(
    members: List<Member>,
    query: String,
    filter: Filter,
    queryChanged: (String, Filter) -> Unit,
    selectMember: (Member) -> Unit,
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SearchTextField(
            query = query,
            filter = filter,
            onValueChange = queryChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
        MemberColumn(
            members = members,
            clickMember = selectMember
        )
    }
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MembersPreviewProvider::class) members: List<Member>) {
    MemberSearchScreen(
        members = members,
        query = "",
        filter = Filter.Normal,
        queryChanged = { _, _ -> }
    ) {}
}
