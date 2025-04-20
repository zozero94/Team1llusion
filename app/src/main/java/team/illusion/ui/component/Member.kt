package team.illusion.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.illusion.MembersPreviewProvider
import team.illusion.data.model.Member
import team.illusion.data.model.displayRemainCount
import team.illusion.data.model.isExpireDate

@Composable
fun MemberColumn(
    modifier: Modifier = Modifier,
    members: List<Member>,
    clickMember: (Member) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "인원 : ${members.size}")
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
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
                            .border(1.dp, color = Color(0xffBDBDBD), shape = RoundedCornerShape(12.dp))
                            .fillMaxWidth()
                            .clickable { clickMember(member) }
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        InfoRow(title = "이름", text = member.name)
                        InfoRow(title = "성별", text = "${member.sex}")
                        InfoRow(title = "휴대폰", text = member.phone)
                        if (member.address.isNotEmpty()) {
                            InfoRow(title = "주소", text = member.address)
                        }
                        InfoRow(title = "시작 날짜", text = member.startDate)
                        InfoRow(title = "남은 날짜", text = buildAnnotatedString {
                            if (member.isExpireDate()) {
                                withStyle(SpanStyle(Color.Red)) {
                                    append(member.endDate)
                                }
                            } else {
                                append(member.endDate)
                            }
                        })
                        InfoRow(title = "남은 횟수", text = buildAnnotatedString {
                            if (member.remainCount.isExpire()) {
                                withStyle(SpanStyle(Color.Red)) {
                                    append("${member.remainCount}")
                                }
                            } else {
                                append(member.displayRemainCount())
                            }
                        }
                        )
                        if (member.comment.isNotEmpty()) {
                            InfoRow(title = "기타", text = member.comment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(title: String, text: AnnotatedString) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 37.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            textAlign = TextAlign.End,
            fontSize = 14.sp,
            maxLines = 1,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .background(color = Color(0xffFAFAFA), shape = RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 14.sp, maxLines = 1, color = Color.Black)
        }
    }


}

@Composable
fun InfoRow(title: String, text: String) {
    InfoRow(title = title, text = buildAnnotatedString { append(text) })
}

@Preview
@Composable
fun InfoRowPreview() {
    InfoRow("이름", "조재영")
}

@Preview
@Composable
private fun Preview(
    @PreviewParameter(MembersPreviewProvider::class) members: List<Member>,
) {
    MemberColumn(members = members, clickMember = {})
}
