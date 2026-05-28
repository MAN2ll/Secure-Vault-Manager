package com.securevault.data.repository

import com.securevault.data.db.VaultDao
import com.securevault.data.model.VaultEntry
import com.securevault.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DecryptedEntry(
    val id: Long,
    val title: String,
    val category: String,
    val username: String,
    val password: String,
    val url: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean
)

@Singleton
class VaultRepository @Inject constructor(
    private val dao: VaultDao,
    private val crypto: CryptoManager
) {

    fun getAllEntries(): Flow<List<DecryptedEntry>> =
        dao.getAllEntries().map { list -> list.map { it.toDecrypted() } }

    fun getFavoriteEntries(): Flow<List<DecryptedEntry>> =
        dao.getFavoriteEntries().map { list -> list.map { it.toDecrypted() } }

    fun searchEntries(query: String): Flow<List<DecryptedEntry>> =
        dao.searchEntries(query).map { list -> list.map { it.toDecrypted() } }

    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()

    fun getEntryCount(): Flow<Int> = dao.getEntryCount()

    suspend fun getEntryById(id: Long): DecryptedEntry? =
        dao.getEntryById(id)?.toDecrypted()

    suspend fun saveEntry(
        id: Long = 0,
        title: String,
        category: String,
        username: String,
        password: String,
        url: String = "",
        notes: String = "",
        isFavorite: Boolean = false
    ): Long {
        val entry = VaultEntry(
            id = id,
            title = title,
            category = category,
            encryptedUsername = crypto.encryptString(username),
            encryptedPassword = crypto.encryptString(password),
            encryptedUrl = if (url.isNotEmpty()) crypto.encryptString(url) else "",
            encryptedNotes = if (notes.isNotEmpty()) crypto.encryptString(notes) else "",
            updatedAt = System.currentTimeMillis(),
            isFavorite = isFavorite
        )
        return dao.insertEntry(entry)
    }

    suspend fun toggleFavorite(entry: DecryptedEntry) {
        val dbEntry = dao.getEntryById(entry.id) ?: return
        dao.updateEntry(dbEntry.copy(isFavorite = !dbEntry.isFavorite))
    }

    suspend fun deleteEntry(id: Long) = dao.deleteEntryById(id)

    private fun VaultEntry.toDecrypted(): DecryptedEntry {
        return DecryptedEntry(
            id = id,
            title = title,
            category = category,
            username = if (encryptedUsername.isNotEmpty()) crypto.decryptString(encryptedUsername) else "",
            password = if (encryptedPassword.isNotEmpty()) crypto.decryptString(encryptedPassword) else "",
            url = if (encryptedUrl.isNotEmpty()) crypto.decryptString(encryptedUrl) else "",
            notes = if (encryptedNotes.isNotEmpty()) crypto.decryptString(encryptedNotes) else "",
            createdAt = createdAt,
            updatedAt = updatedAt,
            isFavorite = isFavorite
        )
    }
}
