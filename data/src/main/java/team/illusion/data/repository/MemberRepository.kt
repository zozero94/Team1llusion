package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import team.illusion.data.awaitGet
import team.illusion.data.awaitGetList
import team.illusion.data.bindDataChanged
import team.illusion.data.bindDataListChanged
import team.illusion.data.datasource.CenterStore
import team.illusion.data.datasource.DateManager
import team.illusion.data.model.Count
import team.illusion.data.model.Member
import team.illusion.data.model.Sex
import team.illusion.data.model.isBeforeDate
import team.illusion.data.model.isExpireDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    firebase: FirebaseDatabase,
    centerStore: CenterStore,
) {

    private val memberReferenceFlow = centerStore.center.map {
        firebase.getReference("member/$it")
    }

    suspend fun registerMember(
        name: String,
        phone: String,
        sex: Sex,
        address: String,
        comment: String,
        startDate: String,
        endDate: String,
        remainCount: Count,
        checkInDate: List<String>,
    ) {
        val newChild = memberReferenceFlow.first().push()
        val member = Member(
            id = newChild.key.orEmpty(),
            name = name,
            phone = phone,
            sex = sex,
            address = address,
            remainCount = remainCount,
            startDate = startDate,
            endDate = endDate,
            comment = comment,
            checkInDate = checkInDate
        )

        newChild.setValue(member).await()
    }

    suspend fun getMembers(): List<Member> {
        return memberReferenceFlow.first().awaitGetList()
    }

    suspend fun getMember(id: String): Member? {
        return memberReferenceFlow.first().child(id).awaitGet()
    }

    fun bindMember(id: String): Flow<Member> {
        return memberReferenceFlow.flatMapLatest { it.child(id).bindDataChanged() }
    }

    suspend fun findMember(id: String?, phone: String): Member? {
        return getMembers().find { it.phone == phone || it.id == id }
    }

    fun bindMembers(): Flow<List<Member>> {
        return memberReferenceFlow.flatMapLatest { it.bindDataListChanged() }
    }

    fun getMembers(phoneNumber: String): Flow<List<Member>> {
        return memberReferenceFlow.flatMapLatest { it.snapshots }.map { snapshot ->
            snapshot.children
                .mapNotNull {
                    it.getValue(Member::class.java)
                }.filter {
                    it.phone.endsWith(phoneNumber)
                }
        }
    }

    suspend fun checkIn(member: Member) {
        val remainCount = if (member.remainCount.count != null) {
            require(member.remainCount.count > 0) { "회원권이 모두 소진 되었습니다." }
            Count(member.remainCount.count - 1)
        } else {
            Count(null)
        }
        require(!member.isExpireDate()) { "기간이 만료 되었습니다." }
        require(member.isBeforeDate()) { "시작일보다 더 빨리 방문해주셨습니다.." }

        editMember(
            member.copy(
                remainCount = remainCount,
                checkInDate = member.checkInDate + listOf(DateManager.today + "/" + DateManager.currentTime())
            )
        )
    }

    suspend fun deleteMember(id: String) {
        memberReferenceFlow.first().child(id).removeValue().await()
    }

    suspend fun editMember(member: Member) {
        memberReferenceFlow.first().child(member.id).updateChildren(
            mapOf(
                "name" to member.name,
                "phone" to member.phone,
                "sex" to member.sex,
                "address" to member.address,
                "comment" to member.comment,
                "startDate" to member.startDate,
                "endDate" to member.endDate,
                "remainCount" to member.remainCount,
                "checkInDate" to member.checkInDate,
            )
        ).await()
    }

    suspend fun deleteAll() {
        memberReferenceFlow.first().removeValue().await()
    }

}
