package team.illusion.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import team.illusion.data.model.Member
import team.illusion.data.model.displayRemainCount
import team.illusion.data.model.isExpireDate

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
                    Text(text = "Id : ${member.id}")
                    Text(text = "이름 : ${member.name}")
                    Text(text = "성별 : ${member.sex}")
                    Text(text = "휴대폰 : ${member.phone}")
                    if (member.address.isNotEmpty()) {
                        Text(text = "주소 : ${member.address}")
                    }
                    Text(text = "옵션 : ${member.option}")
                    Text(text = "시작 날짜 : ${member.startDate}")
                    Text(
                        text = buildAnnotatedString {
                            append("남은 날짜 : ")
                            if (member.isExpireDate()) {
                                withStyle(SpanStyle(Color.Red)) {
                                    append(member.endDate)
                                }
                            } else {
                                append(member.endDate)
                            }
                        }
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("남은 횟수 : ")
                            if (member.remainCount == 0) {
                                withStyle(SpanStyle(Color.Red)) {
                                    append("${member.remainCount}")
                                }
                            } else {
                                append(member.displayRemainCount())
                            }
                        }
                    )
                    if (member.comment.isNotEmpty()) {
                        Text(text = "기타 : ${member.comment}")
                    }
                }
            }
        }
    }
}