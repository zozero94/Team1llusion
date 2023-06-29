package team.illusion.admin.member.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import team.illusion.component.SearchTextField
import team.illusion.data.model.Member

@Composable
fun MemberSearchScreen(
    uiState: MemberSearchUiState,
    queryChanged: (String) -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        SearchTextField(query = uiState.query, onValueChange = queryChanged)
        Spacer(modifier = Modifier.height(16.dp))
        MemberColumn(query = uiState.query, members = uiState.members, searched = uiState.searched) {

        }
    }
}

@Composable
private fun MemberColumn(
    query: String,
    members: List<Member>,
    searched: List<Member>,
    clickMember: (Member) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (members.isEmpty() && searched.isEmpty()) {
            item {
                Text(modifier = Modifier.fillMaxWidth(), text = "데이터 없음", textAlign = TextAlign.Center)
            }
        } else {
            items(if (query.isEmpty()) members else searched) { member ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .clickable { clickMember(member) },
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(imageVector = Icons.Default.Person, contentDescription = null)
                    Text(text = "Id : ${member.id}")
                    Text(text = "이름 : ${member.name}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "휴대폰 : ${member.phone}")
                }
            }
        }
    }
}