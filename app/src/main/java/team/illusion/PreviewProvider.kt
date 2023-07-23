package team.illusion

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import team.illusion.data.model.Count
import team.illusion.data.model.Member
import team.illusion.data.model.Sex

class MembersPreviewProvider : PreviewParameterProvider<List<Member>> {
    override val values: Sequence<List<Member>>
        get() = sequenceOf(listOf(ZERO, TESTER1, TESTER2))
}

class MemberPreviewProvider : PreviewParameterProvider<Member> {
    override val values: Sequence<Member>
        get() = sequenceOf(ZERO, TESTER1, TESTER2)
}

val ZERO = Member(
    id = "1",
    name = "zero",
    phone = "010-3610-8845",
    sex = Sex.Male,
    address = "서울시 광진구",
    remainCount = Count(15),
    startDate = "2023-07-03",
    endDate = "2023-08-03",
    comment = "nothing",
    checkInDate = listOf("2023-07-03")
)

val TESTER1 = Member(
    id = "2",
    name = "tester1",
    phone = "010-1234-8845",
    sex = Sex.Male,
    address = "서울시 논현동",
    remainCount = Count(null),
    startDate = "2023-07-03",
    endDate = "2023-08-03",
    comment = "nothing",
    checkInDate = listOf("2023-07-03")
)

val TESTER2 = Member(
    id = "3",
    name = "tester2",
    phone = "010-4321-8845",
    sex = Sex.Male,
    address = "서울시 노원구",
    remainCount = Count(0),
    startDate = "2023-07-03",
    endDate = "2023-08-03",
    comment = "nothing",
    checkInDate = listOf("2023-07-03")
)
