package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
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

    fun bindMembers(): Flow<List<Member>> {
        return memberReference.bindDataListChanged()
    }

    fun removeMember(member: Member) {
        memberReference.child(member.id)
    }

}