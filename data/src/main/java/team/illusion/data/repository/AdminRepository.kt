package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import team.illusion.data.awaitGet
import team.illusion.data.bindDataChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    firebase: FirebaseDatabase,
) {

    private val adminReference = firebase.getReference("admin")


    fun bindUsePassword() = adminReference.child("use_password").bindDataChanged<Boolean>()

    suspend fun changePassword(password: String) {
        adminReference.child("password").setValue(password).await()
    }

    suspend fun updateUsePassword(usePassword: Boolean) {
        adminReference.child("use_password").setValue(usePassword).await()
    }

    fun bindPassword(): Flow<String> = adminReference.child("password").bindDataChanged<String>()

    suspend fun deleteAll() {
        adminReference.removeValue().await()
    }

    suspend fun getAllowAdmins(): String? {
        return adminReference.child("allows").awaitGet<String>()
    }
}
