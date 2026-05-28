package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.data.repository.DecryptedEntry
import com.securevault.data.repository.VaultRepository
import com.securevault.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: VaultRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    @OptIn(ExperimentalCoroutinesApi::class)
    val entries: StateFlow<List<DecryptedEntry>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) repository.getAllEntries()
            else repository.searchEntries(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entryCount: StateFlow<Int> = repository.getEntryCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategory(category: String?) { _selectedCategory.value = category }

    fun toggleFavorite(entry: DecryptedEntry) {
        viewModelScope.launch { repository.toggleFavorite(entry) }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch { repository.deleteEntry(id) }
    }

    fun lock() { sessionManager.lock() }
}
