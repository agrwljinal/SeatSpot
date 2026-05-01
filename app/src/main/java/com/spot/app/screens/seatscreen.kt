package com.spot.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

val SpotDeep = Color(0xFF1A1A1A)
val SpotGold = Color(0xFFFFD700)
val SpotGoldBright = Color(0xFFFFF0A3)
val SpotBlack = Color(0xFF000000)
val SpotWhite80 = Color(0xCCFFFFFF)
val SpotWhite50 = Color(0x80FFFFFF)
val SpotWhite30 = Color(0x4DFFFFFF)
val SeatFree = Color(0xFF4CAF50)
val SeatTaken = Color(0xFFE91E63)
val SeatHeld = Color(0xFFFF9800)

enum class LocalSeatStatus { AVAILABLE, OCCUPIED, RESERVED, SELECTED }
data class LocalSeat(val id: String, val row: Int, val column: Int, val status: LocalSeatStatus)


@Composable
fun GlassCard(modifier: Modifier = Modifier, cornerRadius: Dp = 24.dp, content: @Composable () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(cornerRadius)).background(Color.White.copy(0.05f)).border(0.5.dp, Color.White.copy(0.1f), RoundedCornerShape(cornerRadius))) {
        content()
    }
}


fun buildLocalSeats(): List<LocalSeat> {
    val rows = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')
    val taken = setOf("A3", "B2", "B4", "C3", "D1", "D5", "E3", "F2", "H4", "I1")
    val reserved = setOf("B1", "E2", "G5")
    return rows.flatMapIndexed { rowIndex, r ->
        (1..6).map { c ->
            val id = "$r$c"
            val status = when (id) {
                in taken -> LocalSeatStatus.OCCUPIED
                in reserved -> LocalSeatStatus.RESERVED
                else -> LocalSeatStatus.AVAILABLE
            }
            LocalSeat(id, rowIndex, c, status)
        }
    }
}

@Composable
fun SeatsScreen(navController: NavController) {
    var selectedSeatId by remember { mutableStateOf("") }
    val seats = remember { buildLocalSeats() }
    val freeCount = seats.count { it.status == LocalSeatStatus.AVAILABLE }

    val inf = rememberInfiniteTransition(label = "bg")
    val bgShift by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(10000, easing = EaseInOutSine), RepeatMode.Reverse), label = "bg2")

    Box(Modifier.fillMaxSize().background(SpotDeep)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(SpotGold.copy(0.08f + bgShift * 0.04f), Color.Transparent), center = Offset(size.width * 0.5f, size.height * 0.1f)),
                radius = size.width * 0.7f, center = Offset(size.width * 0.5f, size.height * 0.1f)
            )
        }

        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(52.dp))
            Column(Modifier.padding(horizontal = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lab Seats", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = SpotWhite80, modifier = Modifier.weight(1f))
                    Row(Modifier.clip(RoundedCornerShape(20.dp)).background(SeatFree.copy(0.15f)).padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        BlinkDot()
                        Spacer(Modifier.width(5.dp))
                        Text("$freeCount free", fontSize = 11.sp, color = SeatFree)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Legend
            Row(Modifier.padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                listOf(SeatFree to "Free", SeatTaken to "Taken", SeatHeld to "Reserved").forEach { (color, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(color))
                        Spacer(Modifier.width(4.dp))
                        Text(label, fontSize = 11.sp, color = SpotWhite50)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("▼  ENTRY", fontSize = 9.sp, color = SpotWhite30, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))

            GlassCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I').forEach { rowChar ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(rowChar.toString(), fontSize = 10.sp, color = SpotWhite30, modifier = Modifier.width(14.dp))
                            seats.filter { it.id.startsWith(rowChar) }.forEach { seat ->
                                GlassSeatTile(seat = seat, isSelected = selectedSeatId == seat.id, modifier = Modifier.weight(1f), onClick = {
                                    if (seat.status == LocalSeatStatus.AVAILABLE) selectedSeatId = seat.id
                                })
                            }
                        }
                    }
                }
                Spacer(Modifier.height(100.dp))
            }
        }

        AnimatedVisibility(visible = selectedSeatId.isNotEmpty(), modifier = Modifier.align(Alignment.BottomCenter)) {
            Box(Modifier.fillMaxWidth().padding(20.dp).height(56.dp).clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(SpotGold, SpotGoldBright))).clickable { navController.navigate("booking/$selectedSeatId") }, contentAlignment = Alignment.Center) {
                Text("Book Seat $selectedSeatId", fontWeight = FontWeight.Bold, color = SpotBlack)
            }
        }
    }
}

@Composable
fun GlassSeatTile(seat: LocalSeat, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val isAvailable = seat.status == LocalSeatStatus.AVAILABLE
    val statusColor = when {
        isSelected -> SpotGold
        seat.status == LocalSeatStatus.OCCUPIED -> SeatTaken
        seat.status == LocalSeatStatus.RESERVED -> SeatHeld
        else -> SeatFree
    }

    Box(modifier.aspectRatio(1f).clip(RoundedCornerShape(10.dp)).background(statusColor.copy(0.1f)).border(0.5.dp, statusColor.copy(0.4f), RoundedCornerShape(10.dp)).clickable(enabled = isAvailable) { onClick() }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Computer, null, tint = statusColor, modifier = Modifier.size(16.dp))
            Text(seat.id, fontSize = 8.sp, color = statusColor)
        }
    }
}

@Composable
fun BlinkDot() {
    val inf = rememberInfiniteTransition()
    val alpha by inf.animateFloat(1f, 0f, infiniteRepeatable(tween(800), RepeatMode.Reverse))
    Box(Modifier.size(6.dp).clip(CircleShape).background(SeatFree.copy(alpha)))
}