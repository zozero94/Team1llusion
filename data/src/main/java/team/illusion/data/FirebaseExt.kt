package team.illusion.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

inline fun <reified T> DatabaseReference.bindItem(): Flow<T> {
    return callbackFlow {
        get().addOnSuccessListener { snapshot ->
            val result = snapshot.getValue(T::class.java)
            if (result == null) {
                cancel()
            } else {
                trySend(result)
            }
        }.addOnCompleteListener {
            cancel()
        }
        awaitClose { cancel() }
    }
}

inline fun <reified T> DatabaseReference.bindDataChanged(): Flow<T> {
    return callbackFlow {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.getValue(T::class.java)
                if (result == null) {
                    cancel()
                } else {
                    trySend(result)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(error.message, Exception(error.details))
            }

        }
        addValueEventListener(eventListener)
        awaitClose { removeEventListener(eventListener) }
    }

}

suspend inline fun <reified T> DatabaseReference.awaitSetValue(value: T) {
    return suspendCoroutine { con ->
        setValue(value).addOnCompleteListener { con.resume(Unit) }
    }
}