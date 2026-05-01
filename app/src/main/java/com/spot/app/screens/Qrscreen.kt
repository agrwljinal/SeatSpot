package com.spot.app.screens

import android.graphics.Bitmap
import android.graphics.Color as AColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.spot.app.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun QRScreen(navController: NavController, seatId: String, timeSlot: String) {
    val slot  = timeSlot.replace("_", " ")
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    val qr    = remember(seatId, slot) { makeQR("SPOT|$seatId|$slot|$today") }

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val alpha by animateFloatAsState(if (appeared) 1f else 0f,
        tween(600), label = "qa")
    val scale by animateFloatAsState(if (appeared) 1f else 0.88f,
        tween(600, easing = EaseOutBack), label = "qs")

    // Shimmer on the QR card
    val inf = rememberInfiniteTransition(label = "sh")
    val shimmer by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(2200, easing = EaseInOutSine),
            RepeatMode.Reverse), label = "shv"
    )

    Box(Modifier.fillMaxSize().background(SpotDeep)) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(SpotGold.copy(0.10f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.3f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(size.width * 0.5f, size.height * 0.3f)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .alpha(alpha)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Header ────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Outlined.ArrowBackIosNew, null,
                        tint = SpotWhite80, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Booking Confirmed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotWhite80,
                        letterSpacing = (-0.5).sp
                    )
                    Text("Show this at the lab entry",
                        fontSize = 12.sp,
                        color = SpotWhite30
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── QR glass card ─────────────────
            Box(
                Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .scale(scale)
                    .clip(RoundedCornerShape(28.dp))
                    .background(GlassWhite10)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                SpotGold.copy(0.3f + shimmer * 0.3f),
                                GlassWhite25,
                                SpotGold.copy(0.1f + shimmer * 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // QR code on white bg
                    if (qr != null) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SpotWhite)
                                .padding(16.dp)
                        ) {
                            Image(
                                bitmap = qr.asImageBitmap(),
                                contentDescription = "QR",
                                modifier = Modifier.size(190.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.fillMaxWidth().height(0.5.dp).background(GlassWhite15))
                    Spacer(Modifier.height(20.dp))

                    // Booking details
                    listOf(
                        Icons.Outlined.Chair    to Pair("Seat",  seatId),
                        Icons.Outlined.Schedule to Pair("Time",  slot),
                        Icons.Outlined.CalendarToday to Pair("Date", today)
                    ).forEach { (icon, pair) ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, null,
                                tint = SpotWhite30,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(pair.first,
                                fontSize = 13.sp,
                                color = SpotWhite30,
                                modifier = Modifier.width(52.dp)
                            )
                            Text(pair.second,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = SpotWhite80
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Warning pill
            Row(
                Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpotWarn.copy(0.08f))
                    .border(0.5.dp, SpotWarn.copy(0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Info, null,
                    tint = SpotWarn, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(10.dp))
                Text("Arrive 5 min before your slot",
                    fontSize = 12.sp,
                    color = SpotWarn
                )
            }

            Spacer(Modifier.height(20.dp))

            // Done button
            Box(
                Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(SpotGold, SpotGoldBright)))
                    .clickable {
                        navController.navigate("seats") {
                            popUpTo("seats") { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Done  ✓",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SpotBlack
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

fun makeQR(content: String): Bitmap? = try {
    val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val bmp  = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
    for (x in 0 until 512) for (y in 0 until 512)
        bmp.setPixel(x, y, if (bits[x, y]) AColor.BLACK else AColor.WHITE)
    bmp
} catch (e: Exception) { null }