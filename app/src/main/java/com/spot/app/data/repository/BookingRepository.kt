package com.spot.app.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.Query
import com.spot.app.data.model.Booking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class BookingRepository {

    private val db = Firebase.firestore
    private val bookingsRef = db.collection("bookings")
    private val seatsRef = db.collection("seats")

    // Create booking and mark seat occupied
    suspend fun createBooking(
        userId: String,
        userName: String,
        seatId: String,
        timeSlot: String,
    ): Result<String> = try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val now = System.currentTimeMillis()
        val bookingId = bookingsRef.document().id

        // Set expiry to end hour of the slot
        val endHour = when {
            timeSlot.contains("11:00 AM") && timeSlot.contains("9:00") -> 11
            timeSlot.contains("1:00 PM") -> 13
            timeSlot.contains("3:00 PM") -> 15
            timeSlot.contains("5:00 PM") -> 17
            timeSlot.contains("7:00 PM") -> 19
            else -> 23
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val booking = Booking(
            bookingId = bookingId,
            userId = userId,
            userName = userName,
            seatId = seatId,
            timeSlot = timeSlot,
            date = date,
            status = "active",
            createdAt = now,
            expiresAt = cal.timeInMillis,
        )

        bookingsRef.document(bookingId).set(booking).await()
        seatsRef.document(seatId).update(
            mapOf(
                "status" to "occupied",
                "occupantId" to userId,
                "occupantName" to userName
            )
        ).await()

        Result.success(bookingId)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // Cancel booking and free seat
    suspend fun cancelBooking(booking: Booking): Result<Unit> = try {
        bookingsRef.document(booking.bookingId)
            .update("status", "cancelled").await()
        seatsRef.document(booking.seatId).update(
            mapOf(
                "status" to "available",
                "occupantId" to "",
                "occupantName" to ""
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

    // Auto-expire bookings whose time slot has passed
    suspend fun expireOldBookings() {
        val now = System.currentTimeMillis()
        val expired = bookingsRef
            .whereEqualTo("status", "active")
            .whereLessThan("expiresAt", now)
            .get().await()

        if (expired.isEmpty) return
        val batch = db.batch()
        for (doc in expired.documents) {
            batch.update(doc.reference, "status", "completed")
            val seatId = doc.getString("seatId") ?: continue
            batch.update(
                seatsRef.document(seatId), mapOf(
                    "status" to "available",
                    "occupantId" to "",
                    "occupantName" to ""
                )
            )
        }
        batch.commit().await()
    }

    // Get all bookings for a user
    suspend fun getUserBookings(userId: String): List<Booking> = try {
        bookingsRef
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Booking::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
