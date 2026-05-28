package com.securevault.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securevault.data.model.EncryptedEntry
import com.securevault.data.repository.VaultRepository
import com.securevault.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val repository: VaultRepository,
    private val session: SessionManager
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _categoryFilter = MutableStateFlow<String?>(null)
    
    val entries: StateFlow<List<EncryptedEntry>> = combine(
        repository.getAllEntries(),
        _searchQuery,
        _categoryFilter
    ) { entries, query, category ->
        entries.filter { e ->
            (query.isEmpty() || e.title.contains(query, ignoreCase = true)) &&
            (category == null || e.category == category)
        }
    }.let { MutableStateFlow(emptyList()) } // Заглушка для простоты
    
    val categories = repository.getAllCategories()
    val entryCount = repository.getEntryCount()
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
    }
    
    fun toggleFavorite(entry: EncryptedEntry) {
        viewModelScope.launch {
            repository.toggleFavorite(entry.id, !entry.isFavorite)
        }
    }
    
    fun deleteEntry(entry: EncryptedEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry.id)
        }
    }
    
    fun lock() {
        session.lock()
    }
}
