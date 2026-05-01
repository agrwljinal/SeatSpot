package com.spot.app.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.spot.app.*

val SLOTS = listOf(
    "9:00 AM – 11:00 AM",
    "11:00 AM – 1:00 PM",
    "1:00 PM – 3:00 PM",
    "3:00 PM – 5:00 PM",
    "5:00 PM – 7:00 PM"
)

@Composable
fun BookingScreen(navController: NavController, seatId: String) {
    var selected by remember { mutableIntStateOf(-1) }
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val headerAlpha by animateFloatAsState(if (appeared) 1f else 0f,
        tween(500), label = "ha")
    val headerSlide by animateFloatAsState(if (appeared) 0f else (-20f),
        tween(500, easing = EaseOutCubic), label = "hs")

    Box(Modifier.fillMaxSize().background(SpotDeep)) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(SpotGold.copy(0.07f), Color.Transparent),
                    center = Offset(size.width * 0.8f, 0f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.8f, 0f)
            )
        }

        Column(Modifier.fillMaxSize()) {

            // ── Header ────────────────────────
            Spacer(Modifier.height(52.dp))
            Row(
                Modifier
                    .padding(horizontal = 20.dp)
                    .alpha(headerAlpha)
                    .offset(y = headerSlide.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Outlined.ArrowBackIosNew, null,
                        tint = SpotWhite80, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Seat $seatId",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpotWhite80,
                        letterSpacing = (-0.5).sp
                    )
                    Text("Pick a time window",
                        fontSize = 12.sp,
                        color = SpotWhite30
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Slot cards ────────────────────
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SLOTS.forEachIndexed { i, slot ->
                    val isSelected = selected == i
                    val cardAlpha by animateFloatAsState(if (appeared) 1f else 0f,
                        tween(400 + i * 80), label = "ca$i")
                    val cardSlide by animateFloatAsState(if (appeared) 0f else 30f,
                        tween(400 + i * 80, easing = EaseOutCubic), label = "cs$i")

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .alpha(cardAlpha)
                            .offset(y = cardSlide.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) SpotGold.copy(0.12f)
                                else GlassWhite10
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) SpotGold.copy(0.6f)
                                else GlassWhite25,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selected = i }
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Time icon in glass circle
                            Box(
                                Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) SpotGold.copy(0.15f)
                                        else GlassWhite5
                                    )
                                    .border(0.5.dp,
                                        if (isSelected) SpotGold.copy(0.4f)
                                        else GlassWhite25,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Schedule, null,
                                    tint = if (isSelected) SpotGold else SpotWhite30,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(slot,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Medium
                                else FontWeight.Normal,
                                color = if (isSelected) SpotWhite80 else SpotWhite50,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Outlined.CheckCircle, null,
                                    tint = SpotGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Confirm button ────────────────
            AnimatedVisibility(
                visible = selected >= 0,
                enter = slideInVertically { it } + fadeIn(tween(300)),
                exit  = slideOutVertically { it } + fadeOut(tween(200))
            ) {
                Box(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(
                                listOf(SpotGold, SpotGoldBright)
                            ))
                            .clickable {
                                if (selected >= 0) {
                                    val slot = SLOTS[selected]
                                        .replace(" ", "_")
                                    navController.navigate("qr/$seatId/$slot")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Confirm Booking",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SpotBlack
                        )
                    }
                }
            }
        }
    }
}