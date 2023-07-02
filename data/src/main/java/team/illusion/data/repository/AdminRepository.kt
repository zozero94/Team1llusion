package team.illusion.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import team.illusion.data.awaitGet
import team.illusion.data.bindDataChanged
import timber.log.Timber
import javax.inject.Inject

class AdminRepository @Inject constructor(
    firebase: FirebaseDatabase
) {

    private val adminReference = firebase.getReference("admin")

    suspend fun initialize() {
        runCatching {
            val usePasswordRef = adminReference.child("use_password")
            val passwordRef = adminReference.child("password")

            val usePassword = usePasswordRef.awaitGet<Boolean>()
            val password = passwordRef.awaitGet<String>()
            if (usePassword == null && password == null) {
                usePasswordRef.setValue(false)
                passwordRef.setValue("")
            }
        }.onFailure {
            Timber.tag("zero:").e(it)
        }
    }


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
}