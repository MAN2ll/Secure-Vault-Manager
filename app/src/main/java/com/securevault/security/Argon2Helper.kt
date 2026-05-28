package com.securevault.security

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Argon2Helper @Inject constructor() {
    private val argon2 = Argon2Factory.create(Argon2Types.ARGON2id)
    
    fun hashPassword(password: String): String {
        return argon2.hash(3, 65536, 1, password.toCharArray())
    }
    
    fun verifyPassword(hash: String, password: String): Boolean {
        return try {
            argon2.verify(hash, password.toCharArray())
        } finally {
            argon2.wipeArray(password.toCharArray())
        }
    }
}
