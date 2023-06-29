package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import team.illusion.data.awaitGetList
import team.illusion.data.awaitSetValue
import team.illusion.data.bindDataListChanged
import team.illusion.data.model.Member
import javax.inject.Inject

class MemberRepository @Inject constructor(
    firebase: FirebaseDatabase
) {
    private val memberReference = firebase.getReference("member")

    suspend fun registerMember(member: Member) {
        val newChild = memberReference.push()
        newChild.awaitSetValue(member.copy(id = newChild.key.orEmpty()))
    }

    suspend fun getMembers(): List<Member> {
        return memberReference.awaitGetList()
    }

    suspend fun findMember(member: Member): Member? {
        return getMembers().find { it.phone == member.phone || it.id == member.id }
    }

    fun bindMembers(): Flow<List<Member>> {
        return memberReference.bindDataListChanged()
    }

    fun removeMember(member: Member) {
        memberReference.child(member.id)
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

    fun checkIn(member: Member) {

    }

}