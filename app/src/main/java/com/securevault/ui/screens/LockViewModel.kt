package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.security.Argon2Helper
import com.securevault.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LockUiState {
    object Idle : LockUiState()
    object Loading : LockUiState()
    object Success : LockUiState()
    data class Error(val message: String) : LockUiState()
}

@HiltViewModel
class LockViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val argon2Helper: Argon2Helper
) : ViewModel() {

    private val _state = MutableStateFlow<LockUiState>(LockUiState.Idle)
    val state: StateFlow<LockUiState> = _state

    val isSetupDone: Boolean get() = sessionManager.isSetupDone()
    val isBiometricEnabled: Boolean get() = sessionManager.isBiometricEnabled()

    fun setupMasterPassword(password: String, confirm: String) {
        if (password.length < 6) {
            _state.value = LockUiState.Error("Пароль должен быть не менее 6 символов")
            return
        }
        if (password != confirm) {
            _state.value = LockUiState.Error("Пароли не совпадают")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = LockUiState.Loading
            try {
                val hash = argon2Helper.hashPassword(password.toCharArray())
                sessionManager.saveMasterHash(hash)
                sessionManager.unlock()
                _state.value = LockUiState.Success
            } catch (e: Exception) {
                _state.value = LockUiState.Error("Ошибка создания пароля")
            }
        }
    }

    fun unlock(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = LockUiState.Loading
            try {
                val hash = sessionManager.getMasterHash()
                if (hash != null && argon2Helper.verifyPassword(hash, password.toCharArray())) {
                    sessionManager.unlock()
                    _state.value = LockUiState.Success
                } else {
                    _state.value = LockUiState.Error("Неверный пароль")
                }
            } catch (e: Exception) {
                _state.value = LockUiState.Error("Ошибка проверки пароля")
            }
        }
    }

    fun unlockWithBiometric() {
        sessionManager.unlock()
        _state.value = LockUiState.Success
    }

    fun resetError() {
        _state.value = LockUiState.Idle
    }
}
