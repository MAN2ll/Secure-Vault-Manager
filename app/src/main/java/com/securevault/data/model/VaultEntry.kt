package com.securevault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Запись в хранилище паролей.
 * Поля [encryptedPassword], [encryptedUsername], [encryptedNotes]
 * хранятся в зашифрованном виде (AES-256-GCM) в SQLite.
 * Только [title] и [category] хранятся открыто для поиска.
 */
@Entity(tableName = "vault_entries")
data class VaultEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,                          // Название (открытое — для поиска)
    val category: String = "Общее",             // Категория (открытое)
    val encryptedUsername: String,              // Логин (зашифровано)
    val encryptedPassword: String,              // Пароль (зашифровано)
    val encryptedUrl: String = "",              // URL (зашифровано)
    val encryptedNotes: String = "",            // Заметки (зашифровано)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
