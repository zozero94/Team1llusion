package team.illusion.data

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminRepository @Inject constructor(
    firebase: FirebaseDatabase
) {

    private val adminReference = firebase.getReference("admin")
    fun bindUsePassword() = adminReference.child("use_password").bindDataChanged<Boolean>()

    suspend fun changePassword(password: String) {
        adminReference.child("password").awaitSetValue(password)
    }

    suspend fun updateUsePassword(usePassword: Boolean) {
        adminReference.child("use_password").awaitSetValue(usePassword)
    }

    fun bindPassword(): Flow<String> = adminReference.child("password").bindDataChanged<String>()
}