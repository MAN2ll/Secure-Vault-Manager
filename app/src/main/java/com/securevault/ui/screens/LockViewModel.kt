package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.security.Argon2Helper
import com.securevault.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val session: SessionManager,
    private val argon2: Argon2Helper
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState
    
    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }
    
    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun setupMasterPassword() {
        val password = _uiState.value.password
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Минимум 6 символов")
            return
        }
        viewModelScope.launch {
            val hash = argon2.hashPassword(password)
            session.saveMasterHash(hash)
            session.unlock()
            _uiState.value = _uiState.value.copy(isSetupComplete = true)
        }
    }
    
    fun unlock() {
        val password = _uiState.value.password
        val storedHash = session.getMasterHash()
        if (argon2.verifyPassword(storedHash, password)) {
            session.unlock()
            _uiState.value = _uiState.value.copy(isUnlocked = true)
        } else {
            _uiState.value = _uiState.value.copy(error = "Неверный пароль")
        }
    }
    
    fun onBiometricSuccess() {
        session.unlock()
        _uiState.value = _uiState.value.copy(isUnlocked = true)
    }
    
    override fun onCleared() {
        super.onCleared()
        // Очистка пароля из памяти
        _uiState.value = _uiState.value.copy(password = "")
    }
}

data class LockUiState(
    val password: String = "",
    val error: String? = null,
    val isSetupComplete: Boolean = false,
    val isUnlocked: Boolean = false,
    val isBiometricEnabled: Boolean = false
)
