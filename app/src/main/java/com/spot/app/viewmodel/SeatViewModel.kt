package com.spot.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spot.app.data.model.Booking
import com.spot.app.data.model.Seat
import com.spot.app.data.repository.BookingRepository
import com.spot.app.data.repository.SeatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeatViewModel : ViewModel() {

    private val seatRepo = SeatRepository()
    private val bookingRepo = BookingRepository()

    // All seats — seatscreen.kt observes this
    private val _seats = MutableStateFlow<List<Seat>>(emptyList())
    val seats: StateFlow<List<Seat>> = _seats

    // Result message — shown as toast/snackbar
    private val _bookingResult = MutableStateFlow<String?>(null)
    val bookingResult: StateFlow<String?> = _bookingResult

    // Current user's bookings
    private val _userBookings = MutableStateFlow<List<Booking>>(emptyList())
    val userBookings: StateFlow<List<Booking>> = _userBookings

    init {
        // Start listening to Firestore in real time
        viewModelScope.launch {
            seatRepo.observeSeats().collect { _seats.value = it }
        }
        // Expire old bookings on app launch
        viewModelScope.launch {
            bookingRepo.expireOldBookings()
        }
    }

    fun bookSeat(seatId: String, userId: String, userName: String, timeSlot: String) {
        viewModelScope.launch {
            val result = bookingRepo.createBooking(userId, userName, seatId, timeSlot)
            _bookingResult.value = if (result.isSuccess)
                "Seat $seatId booked!"
            else
                "Failed: ${result.exceptionOrNull()?.message}"
        }
    }

    fun freeSeat(seatId: String) {
        viewModelScope.launch { seatRepo.freeSeat(seatId) }
    }

    fun cancelBooking(booking: Booking) {
        viewModelScope.launch { bookingRepo.cancelBooking(booking) }
    }

    fun loadUserBookings(userId: String) {
        viewModelScope.launch {
            _userBookings.value = bookingRepo.getUserBookings(userId)
        }
    }

    fun toggleBroken(seat: Seat) {
        viewModelScope.launch { seatRepo.toggleBroken(seat) }
    }

    fun clearBookingResult() { _bookingResult.value = null }
}
