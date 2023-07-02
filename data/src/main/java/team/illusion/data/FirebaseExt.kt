package team.illusion.data

import com.google.firebase.database.*
import com.google.firebase.database.ktx.snapshots
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

inline fun <reified T> DatabaseReference.bindDataListChanged(): Flow<List<T>> = snapshots.mapNotNull { snapshot ->
    snapshot.children.mapNotNull { child ->
        child.getValue(T::class.java)
    }
}


suspend inline fun <reified T> DatabaseReference.awaitGet(): T? = suspendCancellableCoroutine { cont ->
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val parse = snapshot.getValue(T::class.java)
            cont.resume(parse)
        }

        override fun onCancelled(error: DatabaseError) {
            cont.cancel()
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation { removeEventListener(listener) }
}

suspend inline fun <reified T> DatabaseReference.awaitGetList(): List<T> = suspendCancellableCoroutine { cont ->
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val parse = snapshot.children.mapNotNull { it.getValue(T::class.java) }
            cont.resume(parse)
        }

        override fun onCancelled(error: DatabaseError) {
            cont.cancel()
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation { removeEventListener(listener) }
}

suspend inline fun <reified T> Query.awaitGetList(): List<T> = suspendCancellableCoroutine { cont ->
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val parse = snapshot.children.mapNotNull { it.getValue(T::class.java) }
            cont.resume(parse)
        }

        override fun onCancelled(error: DatabaseError) {
            cont.cancel()
        }
    }
    addListenerForSingleValueEvent(listener)
    cont.invokeOnCancellation { removeEventListener(listener) }
}