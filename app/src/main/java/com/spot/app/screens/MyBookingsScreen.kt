package com.spot.app.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.spot.app.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable

// ─── Data model ──────────────────────────────────────────────────────────────

data class BookingItem(
    val bookingId: String = "",
    val seatId: String = "",
    val timeSlot: String = "",
    val date: String = "",
    val status: String = "active"   // "active" or "completed"
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun BookingsScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var bookings by remember { mutableStateOf<List<BookingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load bookings from Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("bookings")
                .whereEqualTo("userId", uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        bookings = snapshot.documents.mapNotNull { doc ->
                            BookingItem(
                                bookingId = doc.id,
                                seatId    = doc.getString("seatId") ?: "",
                                timeSlot  = doc.getString("timeSlot") ?: "",
                                date      = doc.getString("date") ?: "",
                                status    = doc.getString("status") ?: "active"
                            )
                        }
                        isLoading = false
                    }
                }
        } else {
            isLoading = false
        }
    }

    val activeBookings    = bookings.filter { it.status == "active" }
    val completedBookings = bookings.filter { it.status == "completed" }

    Box(Modifier.fillMaxSize().background(color = SpotDeep)) {

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Header ────────────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = null,
                        tint = SpotWhite80,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "My Bookings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotWhite80,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Your seat history",
                        fontSize = 12.sp,
                        color = SpotWhite30
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Stat cards ────────────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple(bookings.size.toString(),    "Total",     SpotGold),
                    Triple(activeBookings.size.toString(), "Active", SpotGoldBright),
                    Triple(completedBookings.size.toString(), "Done", SpotWhite30)
                ).forEach { (n, l, c) ->
                    GlassCard(modifier = Modifier.weight(1f), cornerRadius = 16.dp) {
                        Column(
                            Modifier.padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(n, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c)
                            Spacer(Modifier.height(2.dp))
                            Text(l, fontSize = 11.sp, color = SpotWhite30, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SpotGold, modifier = Modifier.size(32.dp))
                }
            } else if (bookings.isEmpty()) {
                // Empty state
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.EventBusy,
                            contentDescription = null,
                            tint = SpotWhite30,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("No bookings yet", fontSize = 16.sp, color = SpotWhite30)
                        Text("Book a seat to see it here", fontSize = 13.sp, color = SpotWhite30.copy(alpha = 0.6f))
                    }
                }
            } else {
                // ── Active bookings ───────────────────────────────────────────
                if (activeBookings.isNotEmpty()) {
                    Text(
                        "ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SpotGold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    activeBookings.forEach { booking ->
                        BookingCard(booking = booking, navController = navController)
                        Spacer(Modifier.height(10.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // ── Completed bookings ────────────────────────────────────────
                if (completedBookings.isNotEmpty()) {
                    Text(
                        "COMPLETED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SpotWhite30,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    completedBookings.forEach { booking ->
                        BookingCard(booking = booking, navController = navController)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ─── Booking card ─────────────────────────────────────────────────────────────

@Composable
fun BookingCard(booking: BookingItem, navController: NavController) {
    val isActive = booking.status == "active"

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        cornerRadius = 20.dp
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Seat ${booking.seatId}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SpotWhite80
                        )
                        Spacer(Modifier.width(8.dp))
                        if (isActive) {
                            // Active badge
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SpotGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SpotGold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(booking.timeSlot, fontSize = 13.sp, color = SpotWhite30)
                    Text(booking.date, fontSize = 12.sp, color = SpotWhite30.copy(alpha = 0.6f))
                }

                // View QR button — only for active bookings
                if (isActive) {
                    val slot = booking.timeSlot.replace(" ", "_")
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SpotGold.copy(alpha = 0.12f))
                            .clickable {
                                navController.navigate("qr/${booking.seatId}/$slot")
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.QrCode,
                                contentDescription = "View QR",
                                tint = SpotGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("QR", fontSize = 13.sp, color = SpotGold, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}