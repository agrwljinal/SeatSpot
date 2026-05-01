package com.spot.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.spot.app.*
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun LoginScreen(navController: NavController) {

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("seats") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
    var enrollment by remember { mutableStateOf("") }
    var appeared   by remember { mutableStateOf(false) }
    var btnPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(80); appeared = true }

    val alpha  by animateFloatAsState(if (appeared) 1f else 0f,
        tween(900, easing = EaseOutQuart), label = "a")
    val slideY by animateFloatAsState(if (appeared) 0f else 60f,
        tween(900, easing = EaseOutQuart), label = "s")
    val btnScale by animateFloatAsState(if (btnPressed) 0.95f else 1f,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh), label = "b")

    // Slow-drifting orb animation
    val inf = rememberInfiniteTransition(label = "orb")
    val orbFloat by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(8000, easing = EaseInOutSine),
            RepeatMode.Reverse), label = "of")

    Box(
        Modifier
            .fillMaxSize()
            .background(SpotDeep)
    ) {

        // ── Ambient background orbs ────────────
        Canvas(Modifier.fillMaxSize()) {
            // Large blurred gold orb — top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SpotGold.copy(alpha = 0.12f + orbFloat * 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * 0.15f)
            )
            // Smaller orb — bottom left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SpotWhite.copy(alpha = 0.04f + orbFloat * 0.02f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.82f),
                    radius = size.width * 0.4f
                ),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.1f, size.height * 0.82f)
            )
        }

        // ── Main content ──────────────────────
        Column(
            Modifier
                .fillMaxSize()
                .alpha(alpha)
                .offset(y = slideY.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo mark — glass ring with gold center
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                // Outer glass ring
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GlassWhite10)
                        .border(1.dp, GlassWhite25, CircleShape)
                )
                // Gold inner dot
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(SpotGoldBright, SpotGold)
                            )
                        )
                )
            }

            Spacer(Modifier.height(28.dp))

            Text("SeatSpot",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = SpotWhite80,
                letterSpacing = (-2).sp
            )
            Spacer(Modifier.height(6.dp))
            Text("Your lab seat, reserved.",
                fontSize = 14.sp,
                color = SpotWhite30,
                letterSpacing = 0.2.sp
            )

            Spacer(Modifier.height(52.dp))

            // ── Glass input card ───────────────
            GlassCard(cornerRadius = 20.dp) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

                    Text("ENROLLMENT ID",
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = SpotGold
                    )
                    Spacer(Modifier.height(10.dp))

                    BasicTextField(
                        value = enrollment,
                        onValueChange = { enrollment = it },
                        textStyle = TextStyle(
                            color = SpotWhite80,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        decorationBox = { inner ->
                            Box {
                                if (enrollment.isEmpty())
                                    Text("0000000000",
                                        color = SpotWhite30,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 1.sp
                                    )
                                inner()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))
                    // Thin gold underline
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(
                                if (enrollment.isNotEmpty()) SpotGold.copy(0.7f)
                                else GlassWhite25
                            )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Continue button ────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(btnScale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(SpotGold, SpotGoldBright)
                        )
                    )
                    .clickable {
                        btnPressed = true
                        if (enrollment.isNotBlank()) {
                            val email = "${enrollment.trim().lowercase()}@labseat.app"
                            com.google.firebase.auth.FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(email, "spot2024")
                                .addOnSuccessListener {
                                    navController.navigate(route = "seats") {
                                        popUpTo(route = "login") { inclusive = true }
                                    }
                                }
                                .addOnFailureListener {
                                    // enrollment not found — you can add an error message here later
                                }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Continue →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SpotBlack,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(Modifier.height(20.dp))
            Text("Lab access only  ·  Institutional ID required",
                fontSize = 11.sp,
                color = SpotWhite30,
                textAlign = TextAlign.Center
            )
        }
    }
}