package com.spot.app.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun ProfileScreen(navController: NavController) {

    Box(Modifier.fillMaxSize().background(SpotDeep)) {

        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(SpotGold.copy(0.09f), Color.Transparent),
                    center = Offset(size.width * 0.5f, 0f),
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.5f, 0f)
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // ── Avatar ────────────────────────
            Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GlassWhite10)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(SpotGold.copy(0.6f), GlassWhite25)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = SpotGold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("Student", fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                color = SpotWhite80)
            Spacer(Modifier.height(4.dp))
            Text("student@igdtuw.ac.in", fontSize = 13.sp, color = SpotWhite30)

            Spacer(Modifier.height(28.dp))

            // ── Stats row ─────────────────────
            Row(
                Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("1" to "Total", "1" to "Active").forEach { (n, l) ->
                    GlassCard(modifier = Modifier.weight(1f), cornerRadius = 16.dp) {
                        Column(
                            Modifier.padding(vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(n, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                                color = SpotGold)
                            Spacer(Modifier.height(2.dp))
                            Text(l, fontSize = 11.sp, color = SpotWhite30,
                                letterSpacing = 0.5.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Account section ───────────────
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                cornerRadius = 20.dp
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    GlassInfoRow(Icons.Outlined.Badge,    "enrollment number")
                    GlassDivider()
                    GlassInfoRow(Icons.Outlined.Email,    "student@igdtuw.ac.in")
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Settings section ──────────────
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                cornerRadius = 20.dp
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    GlassSettingsRow(Icons.Outlined.Notifications, "Notifications") {}
                    GlassDivider()
                    GlassSettingsRow(Icons.Outlined.Lock, "Privacy & Security") {}
                    GlassDivider()
                    GlassSettingsRow(Icons.Outlined.Help, "Help") {}
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Logout ────────────────────────
            Box(
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpotDanger.copy(0.07f))
                    .border(0.5.dp, SpotDanger.copy(0.25f), RoundedCornerShape(16.dp))
                    .clickable {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        navController.navigate(route = "login") {
                            popUpTo(id = 0) { inclusive = true }
                        }
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Logout, null,
                        tint = SpotDanger, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Log out",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SpotDanger
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("SeatSpot v1.0", fontSize = 11.sp, color = SpotWhite30.copy(0.5f))
            Spacer(Modifier.height(90.dp))
        }
    }
}

// ── Shared profile row components ─────────────

@Composable
fun GlassInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SpotWhite30, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = SpotWhite80)
    }
}

@Composable
fun GlassSettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SpotWhite30, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 14.sp, color = SpotWhite80, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, null,
            tint = SpotWhite30, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun GlassDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(0.5.dp)
            .background(GlassWhite15)
    )
}