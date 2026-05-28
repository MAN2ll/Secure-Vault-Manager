package com.securevault.security

import de.mkammerer.argon2.Argon2Factory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хеширование мастер-пароля через Argon2id.
 *
 * Параметры (OWASP рекомендации для мобильных устройств):
 *   - memory:     64 МБ  (65536 КБ)
 *   - iterations: 3
 *   - parallelism: 1
 *
 * Argon2id — гибридный вариант, устойчивый как к атакам по времени (Argon2i),
 * так и к атакам по памяти с GPU (Argon2d).
 */
@Singleton
/* class Argon2Helper @Inject constructor() {

    companion object {
        private const val MEMORY_COST = 65536  // 64 MB в KB
        private const val ITERATIONS = 3
        private const val PARALLELISM = 1
    }

    private val argon2 = Argon2Factory.createAdvanced(Argon2Factory.Argon2Types.ARGON2id)

    /**
     * Хеширует пароль. Возвращает строку в формате PHC (содержит соль и параметры).
     * Соль генерируется автоматически при каждом вызове.
     */
    fun hashPassword(password: CharArray): String {
        return try {
            argon2.hash(ITERATIONS, MEMORY_COST, PARALLELISM, password)
        } finally {
            argon2.wipeArray(password) // немедленно затираем пароль из памяти
        }
    }

    /**
     * Верифицирует пароль против сохранённого PHC-хеша.
     * Безопасно к атакам по времени (constant-time comparison внутри libargon2).
     */
    fun verifyPassword(hash: String, password: CharArray): Boolean {
        return try {
            argon2.verify(hash, password)
        } finally {
            argon2.wipeArray(password) // немедленно затираем пароль из памяти
        }
    }
} */
