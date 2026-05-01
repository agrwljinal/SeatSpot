package com.spot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.spot.app.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SpotTheme { SpotApp() } }
    }
}

@Composable
fun SpotApp() {
    val nav = rememberNavController()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val showNav = route in listOf("seats", "profile")

    Scaffold(
        containerColor = SpotDeep,
        bottomBar = { if (showNav) SpotNavBar(nav, route) }
    ) { padding ->
        NavHost(
            navController     = nav,
            startDestination  = "login",
            modifier          = Modifier.padding(padding),
            enterTransition   = {
                fadeIn(tween(280)) +
                        slideInVertically(tween(280, easing = EaseOutCubic)) { it / 12 }
            },
            exitTransition    = {
                fadeOut(tween(200)) +
                        slideOutVertically(tween(200, easing = EaseInCubic)) { -(it / 12) }
            },
            popEnterTransition = {
                fadeIn(tween(280)) +
                        slideInVertically(tween(280, easing = EaseOutCubic)) { -(it / 12) }
            },
            popExitTransition  = {
                fadeOut(tween(200)) +
                        slideOutVertically(tween(200, easing = EaseInCubic)) { it / 12 }
            }
        ) {
            composable("login")   { LoginScreen(nav) }
            composable("seats")   { SeatsScreen(nav) }
            composable("profile") { ProfileScreen(nav) }
            composable("booking/{seatId}") { back ->
                val id = back.arguments?.getString("seatId") ?: "A1"
                BookingScreen(nav, id)
            }
            composable("qr/{seatId}/{timeSlot}") { back ->
                val id   = back.arguments?.getString("seatId")   ?: ""
                val slot = back.arguments?.getString("timeSlot") ?: ""
                QRScreen(nav, id, slot)
            }
            composable(route = "my_bookings") {
                BookingsScreen(navController = nav)
            }
        }
    }
}

// ── Glass bottom nav bar ──────────────────────

@Composable
fun SpotNavBar(nav: NavController, current: String?) {
    val items = listOf(
    Triple("seats", Icons.Outlined.GridView, "Seats"),
    Triple("my_bookings", Icons.Outlined.BookOnline, "Bookings"),
    Triple("profile", Icons.Outlined.Person, "Profile")
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassWhite10)
                .border(0.5.dp, GlassWhite25, RoundedCornerShape(24.dp))
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (route, icon, label) ->
                val active = current == route
                Column(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (!active) nav.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo("seats") { saveState = true }
                            }
                        }
                        .padding(horizontal = 28.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        icon, label,
                        tint = if (active) SpotGold else SpotWhite30,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (active) SpotGold else SpotWhite30,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════
//  SHARED GLASS COMPONENTS
//  (used across all screens — lives here so
//   every screen file can access them)
// ═══════════════════════════════════════════════

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(GlassWhite10)
            .border(0.5.dp, GlassWhite25, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

@Composable
fun GlassIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(GlassWhite10)
            .border(0.5.dp, GlassWhite25, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
        content = { content() }
    )
}