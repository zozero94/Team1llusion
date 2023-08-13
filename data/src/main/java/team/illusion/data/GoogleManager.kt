package team.illusion.data

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
) {

    private val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(team.illusion.data.R.string.default_web_client_id))
        .requestEmail()
        .build()

    val client = GoogleSignIn.getClient(context, options)

    suspend fun logout() {
        client.signOut().await()
    }

    suspend fun signIn(data: Intent?): FirebaseUser? {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return firebaseAuth.signInWithCredential(credential).await().user
    }

    fun getCurrentLoginUserEmail(): String {
        return GoogleSignIn.getLastSignedInAccount(context)?.email.orEmpty()
    }

}
