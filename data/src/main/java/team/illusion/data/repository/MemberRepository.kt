package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import team.illusion.data.awaitSetValue
import team.illusion.data.model.Member
import javax.inject.Inject

class MemberRepository @Inject constructor(
    firebase: FirebaseDatabase
) {
    private val memberReference = firebase.getReference("member")

    suspend fun registerMember(member: Member) {
        memberReference.child(member.key).awaitSetValue(member)
    }

}