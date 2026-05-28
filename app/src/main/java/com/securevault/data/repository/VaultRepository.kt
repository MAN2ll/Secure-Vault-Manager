package com.securevault.data.repository

import com.securevault.data.db.VaultDao
import com.securevault.data.model.EncryptedEntry
import com.securevault.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepository @Inject constructor(
    private val dao: VaultDao,
    private val crypto: CryptoManager
) {
    fun getAllEntries(): Flow<List<EncryptedEntry>> = dao.getAll()
    
    fun searchEntries(query: String): Flow<List<EncryptedEntry>> = 
        dao.search("%$query%")
    
    fun getAllCategories(): Flow<List<String>> = dao.getCategories()
    
    suspend fun getEntryById(id: Long): EncryptedEntry? = dao.getById(id)
    
    suspend fun saveEntry(entry: EncryptedEntry) {
        // Шифруем чувствительные поля
        val encrypted = entry.copy(
            username = crypto.encryptString(entry.username),
            password = crypto.encryptString(entry.password),
            notes = crypto.encryptString(entry.notes)
        )
        dao.upsert(encrypted)
    }
    
    suspend fun deleteEntry(id: Long) = dao.delete(id)
    
    suspend fun toggleFavorite(id: Long, favorite: Boolean) {
        dao.updateFavorite(id, favorite)
    }
    
    fun getEntryCount(): Flow<Int> = dao.getCount()
}
