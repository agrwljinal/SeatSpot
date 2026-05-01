package com.spot.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.spot.app.data.model.Seat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SeatRepository {

    private val db = Firebase.firestore
    private val seatsRef = db.collection("seats")

    // Real-time listener — emits fresh seat list on every change
    fun observeSeats(): Flow<List<Seat>> = callbackFlow {
        val listener = seatsRef.addSnapshotListener { snapshot, error ->
            if ((error != null) || (snapshot == null)) return@addSnapshotListener
            val seats = snapshot.documents.asSequence()
                .mapNotNull { it.toObject(Seat::class.java) }
                .sortedWith(compareBy({ it.row }, { it.col }))
                .toList()
            trySend(seats)
        }
        awaitClose { listener.remove() }
    }

    // Reserve a seat — with conflict check to prevent double booking
    suspend fun reserveSeat(
        seatId: String,
        userId: String,
        userName: String,
    ): Result<Unit> = try {
        db.runTransaction { transaction ->
            val doc = transaction[seatsRef.document(seatId)]
            val currentStatus = doc.getString("status") ?: "available"
            if (currentStatus != "available") {
                throw Exception("Seat $seatId is no longer available")
            }
            transaction.update(
                seatsRef.document(seatId), mapOf(
                    "status" to "occupied",
                    "occupantId" to userId,
                    "occupantName" to userName,
                )
            )
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Free a seat
    suspend fun freeSeat(seatId: String): Result<Unit> = try {
        seatsRef.document(seatId).update(
            mapOf(
                "status" to "available",
                "occupantId" to "",
                "occupantName" to ""
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Admin: toggle broken/available
    suspend fun toggleBroken(seat: Seat): Result<Unit> = try {
        val newStatus = if (seat.status == "broken") "available" else "broken"
        seatsRef.document(seat.seatId).update("status", newStatus).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Run ONCE to seed all 35 seats (A–E rows, 7 cols)
    suspend fun seedSeats() {
        val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")
        val batch = db.batch()
        for (row in rows) {
            for (col in 1..6) {
                val seatId = "$row$col"
                val seat = Seat(
                    seatId = seatId,
                    row = row,
                    col = col,
                    status = "available"
                )
                batch[seatsRef.document(seatId)] = seat
            }
        }
        batch.commit().await()
    }
}
