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
        MemberColumn(members = if (uiState.query.isEmpty()) uiState.members else uiState.searched) {

        }
    }
}

@Composable
fun MemberColumn(
    modifier: Modifier = Modifier,
    members: List<Member>,
    clickMember: (Member) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (members.isEmpty()) {
            item {
                Text(modifier = Modifier.fillMaxWidth(), text = "데이터 없음", textAlign = TextAlign.Center)
            }
        } else {
            items(members) { member ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .clickable { clickMember(member) }
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(imageVector = Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Id : ${member.id}")
                    Text(text = "이름 : ${member.name}")
                    Text(text = "성별 : ${member.sex}")
                    Text(text = "휴대폰 : ${member.phone}")
                    Text(text = "주소 : ${member.address}")
                    Text(text = "옵션 : ${member.option}")
                    Text(text = "남은 기간 : ${member.endDate}")
                    Text(text = "남은 횟수 : ${member.remainCount}")
                    Text(text = "기타 : ${member.comment}")
                }
            }
        }
    }
}