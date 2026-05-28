package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.data.repository.DecryptedEntry
import com.securevault.data.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val repository: VaultRepository
) : ViewModel() {

    private val _entry = MutableStateFlow<DecryptedEntry?>(null)
    val entry: StateFlow<DecryptedEntry?> = _entry

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun loadEntry(id: Long) {
        viewModelScope.launch {
            _entry.value = repository.getEntryById(id)
        }
    }

    fun saveEntry(
        id: Long = 0,
        title: String,
        category: String,
        username: String,
        password: String,
        url: String,
        notes: String,
        isFavorite: Boolean
    ) {
        if (title.isBlank() || password.isBlank()) return
        viewModelScope.launch {
            repository.saveEntry(
                id = id,
                title = title,
                category = category,
                username = username,
                password = password,
                url = url,
                notes = notes,
                isFavorite = isFavorite
            )
            _saved.value = true
        }
    }

    fun generatePassword(
        length: Int = 20,
        includeUpper: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()_+-=[]{}|;:,.<>?"

        var chars = lower
        if (includeUpper) chars += upper
        if (includeDigits) chars += digits
        if (includeSymbols) chars += symbols

        return (1..length).map { chars.random() }.joinToString("")
    }
}
