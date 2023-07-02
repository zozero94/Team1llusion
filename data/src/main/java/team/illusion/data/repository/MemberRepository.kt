package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import team.illusion.data.DateManager
import team.illusion.data.awaitGet
import team.illusion.data.awaitGetList
import team.illusion.data.bindDataListChanged
import team.illusion.data.model.Member
import team.illusion.data.model.Options
import team.illusion.data.model.Sex
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
        )

        newChild.setValue(member).await()
    }

    suspend fun getMembers(): List<Member> {
        return memberReference.awaitGetList()
    }

    suspend fun getMember(id: String): Member? {
        return memberReference.child(id).awaitGet()
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
        if (remainCount != null && remainCount < 0) throw IllegalStateException("모든 횟수를 사용했습니다.")
        editMember(member.copy(remainCount = remainCount))
    }

    suspend fun deleteMember(id: String) {
        memberReference.child(id).removeValue().await()
    }

    suspend fun editMember(member: Member) {
        deleteMember(member.id)
        registerMember(
            name = member.name,
            phone = member.phone,
            sex = member.sex,
            option = member.option,
            enableExtraOption = member.enableExtraOption,
            address = member.address,
            comment = member.comment,
            startDate = member.startDate,
            remainCount = member.remainCount
        )
    }

    suspend fun deleteAll() {
        memberReference.removeValue().await()
    }

}