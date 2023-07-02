package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import team.illusion.data.*
import team.illusion.data.model.*
import javax.inject.Inject

class MemberRepository @Inject constructor(
    firebase: FirebaseDatabase
) {
    private val memberReference = firebase.getReference("member")

    suspend fun registerMember(
        name: String,
        phone: String,
        sex: Sex,
        option: Options,
        enableExtraOption: Boolean,
        address: String,
        comment: String,
        startDate: String,
        remainCount: Int?,
        checkInDate: List<String>
    ) {
        val newChild = memberReference.push()
        val member = Member(
            id = newChild.key.orEmpty(),
            name = name,
            phone = phone,
            sex = sex,
            address = address,
            option = option,
            enableExtraOption = enableExtraOption,
            remainCount = remainCount,
            startDate = startDate,
            endDate = DateManager.calculateDateAfterMonths(
                target = startDate,
                months = if (enableExtraOption) 3 else 1
            ),
            comment = comment,
            checkInDate = checkInDate
        )

        newChild.setValue(member).await()
    }

    suspend fun getMembers(): List<Member> {
        return memberReference.awaitGetList()
    }

    suspend fun getMember(id: String): Member? {
        return memberReference.child(id).awaitGet()
    }

    fun bindMember(id: String): Flow<Member> {
        return memberReference.child(id).bindDataChanged()
    }

    suspend fun findMember(id: String?, phone: String): Member? {
        return getMembers().find { it.phone == phone || it.id == id }
    }

    fun bindMembers(): Flow<List<Member>> {
        return memberReference.bindDataListChanged()
    }

    fun getMembers(phoneNumber: String): Flow<List<Member>> {
        return memberReference.snapshots.map { snapshot ->
            snapshot.children
                .mapNotNull {
                    it.getValue(Member::class.java)
                }.filter {
                    it.phone.contains(phoneNumber)
                }
        }
    }

    suspend fun checkIn(member: Member) {
        val remainCount = member.remainCount?.minus(1)
        if (remainCount != null) {
            require(remainCount > 0) { "회원권이 모두 소진 되었습니다." }
        }
        require(!member.isExpireDate()) { "기간이 만료 되었습니다." }
        require(member.isBeforeDate()) { "시작일보다 더 빨리 방문해주셨습니다.." }
        require(!member.checkInDate.contains(DateManager.today)) { "이미 체크인되어있습니다." }

        editMember(
            member.copy(
                remainCount = remainCount,
                checkInDate = member.checkInDate + listOf(DateManager.today)
            )
        )
    }

    suspend fun deleteMember(id: String) {
        memberReference.child(id).removeValue().await()
    }

    suspend fun editMember(member: Member) {
        memberReference.child(member.id).updateChildren(
            mapOf(
                "name" to member.name,
                "phone" to member.phone,
                "sex" to member.sex,
                "option" to member.option,
                "enableExtraOption" to member.enableExtraOption,
                "address" to member.address,
                "comment" to member.comment,
                "startDate" to member.startDate,
                "remainCount" to member.remainCount,
                "checkInDate" to member.checkInDate
            )
        ).await()
    }

    suspend fun deleteAll() {
        memberReference.removeValue().await()
    }

}