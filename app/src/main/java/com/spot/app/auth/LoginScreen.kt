package com.spot.app.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Tab options ──────────────────────────────────────────────────────────────

enum class LoginTab { ENROLLMENT, EMAIL }

// ─── LoginScreen ──────────────────────────────────────────────────────────────

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate away as soon as login succeeds
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    var selectedTab by remember { mutableStateOf(LoginTab.ENROLLMENT) }
    var email by remember { mutableStateOf("") }
    var enrollmentNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Clear error when user switches tab or starts typing
    LaunchedEffect(selectedTab, email, enrollmentNumber, password) {
        viewModel.clearError()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // ── Brand header ──────────────────────────────────────────────────────
        Text(
            text = "LabSeat",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Stop Roaming, Start Coding",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // ── Tab switcher ──────────────────────────────────────────────────────
        LoginTabSwitcher(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
                password = ""       // clear password when switching tabs
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Input fields ──────────────────────────────────────────────────────
        if (selectedTab == LoginTab.ENROLLMENT) {
            OutlinedTextField(
                value = enrollmentNumber,
                onValueChange = { enrollmentNumber = it },
                label = { Text("Enrollment Number") },
                placeholder = { Text("e.g. 2023CSE001") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        } else {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("you@example.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    text = if (passwordVisible) "Hide" else "Show",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    triggerLogin(selectedTab, email, enrollmentNumber, password, viewModel)
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Error message ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Login button ──────────────────────────────────────────────────────
        Button(
            onClick = {
                focusManager.clearFocus()
                triggerLogin(selectedTab, email, enrollmentNumber, password, viewModel)
            },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Mini seat grid preview (M1 design element) ────────────────────────
        MiniSeatGridPreview()
    }
}

// ─── Tab Switcher ─────────────────────────────────────────────────────────────

@Composable
fun LoginTabSwitcher(
    selectedTab: LoginTab,
    onTabSelected: (LoginTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ) {
        LoginTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.surface
                        else Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (tab == LoginTab.ENROLLMENT) "Enrollment No." else "Email",
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── Mini seat grid preview ───────────────────────────────────────────────────
// Decorative preview matching the M1 login screen design spec

@Composable
fun MiniSeatGridPreview() {
    val colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), // available
        MaterialTheme.colorScheme.error.copy(alpha = 0.2f),    // occupied
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),  // selected
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) // empty
    )
    // 3x5 mini grid — purely decorative
    val pattern = listOf(0, 1, 0, 1, 0, 0, 2, 1, 0, 0, 0, 1, 0, 1, 0)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Lab availability",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        (0 until 3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (0 until 5).forEach { col ->
                    Box(
                        modifier = Modifier
                            .size(width = 28.dp, height = 20.dp)
                            .background(
                                color = colors[pattern[row * 5 + col]],
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────

private fun triggerLogin(
    tab: LoginTab,
    email: String,
    enrollmentNumber: String,
    password: String,
    viewModel: AuthViewModel
) {
    if (tab == LoginTab.EMAIL) {
        viewModel.loginWithEmail(email, password)
    } else {
        viewModel.loginWithEnrollment(enrollmentNumber, password)
    }
}