package com.spot.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ─── UI State ────────────────────────────────────────────────────────────────

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

// ─── ViewModel ───────────────────────────────────────────────────────────────

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Check on startup — if user is already signed in, skip the login screen
    init {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    // ── Email / password login ────────────────────────────────────────────────

    fun loginWithEmail(email: String, password: String) {
        if (!validateEmailInputs(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "No account found for this email.")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Incorrect password. Please try again.")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Login failed: ${e.localizedMessage}")
                }
            }
        }
    }

    // ── Enrollment number login ───────────────────────────────────────────────
    // Enrollment numbers are mapped to a Firebase-compatible email format:
    // e.g. "2023CSE001" → "2023cse001@labseat.app"
    // Make sure these accounts were created in Firebase Auth with this same format.

    fun loginWithEnrollment(enrollmentNumber: String, password: String) {
        if (!validateEnrollmentInputs(enrollmentNumber, password)) return

        val email = enrollmentToEmail(enrollmentNumber)
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Enrollment number not registered.")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Incorrect password. Please try again.")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Login failed: ${e.localizedMessage}")
                }
            }
        }
    }

    // ── Signup ────────────────────────────────────────────────────────────────

    fun signUpWithEmail(email: String, password: String) {
        if (!validateEmailInputs(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Sign up failed: ${e.localizedMessage}")
                }
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    fun logout() {
        auth.signOut()
        _uiState.update { AuthUiState() } // reset to default (not logged in)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun enrollmentToEmail(enrollment: String): String {
        return "${enrollment.trim().lowercase()}@labseat.app"
    }

    private fun validateEmailInputs(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please enter your email.") }
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
                false
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please enter your password.") }
                false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
                false
            }
            else -> true
        }
    }

    private fun validateEnrollmentInputs(enrollment: String, password: String): Boolean {
        return when {
            enrollment.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please enter your enrollment number.") }
                false
            }
            password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please enter your password.") }
                false
            }
            else -> true
        }
    }
}