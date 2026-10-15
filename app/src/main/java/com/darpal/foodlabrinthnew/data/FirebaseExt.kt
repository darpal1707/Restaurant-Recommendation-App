package com.darpal.foodlabrinthnew.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Live snapshots of a query; the listener is removed when the collector stops. */
fun Query.snapshots(): Flow<DataSnapshot> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySend(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    addValueEventListener(listener)
    awaitClose { removeEventListener(listener) }
}

/** Children of a snapshot as (key, map) pairs, skipping anything that isn't an object. */
fun DataSnapshot.childMaps(): Sequence<Pair<String?, Map<*, *>>> =
    children.asSequence().mapNotNull { child -> (child.value as? Map<*, *>)?.let { child.key to it } }
