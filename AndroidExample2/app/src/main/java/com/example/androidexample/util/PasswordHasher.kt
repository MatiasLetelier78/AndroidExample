package com.example.androidexample.util

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {

    fun hashPassword(password: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = sha256(password, salt)
        return "${salt.toHex()}:${hash.toHex()}"
    }

    fun verify(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false

        return try {
            val salt = parts[0].hexToByteArray()
            val esperado = parts[1].hexToByteArray()
            val calculado = sha256(password, salt)
            MessageDigest.isEqual(calculado, esperado)   // comparación segura
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun sha256(password: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        return digest.digest(password.toByteArray(Charsets.UTF_8))
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToByteArray(): ByteArray {
        require(length % 2 == 0) { "Hex inválido" }
        return ByteArray(length / 2) { i ->
            substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}