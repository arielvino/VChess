package net.av.vchess.network

import kotlinx.coroutines.runBlocking
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encryptor {
    private var symmetricKey: String
    private var connector: IConnector

    constructor(connector: HostConnector) {
        this.connector = connector
        val keyPair: Pair<String, String> = RsaFactory.generateRSAKeyPair()
        println("Key pair generated.")
        connector.send(keyPair.first)
        println("Public key sent.")
        val encryptedKey = connector.receive()
        println("Symmetric Key received.")
        symmetricKey = RsaFactory.decryptWithPrivateKey(encryptedKey, keyPair.second)
        println("Symmetric key decrypted and saved.")
    }

    constructor(connector: ClientConnector) {
        this.connector = connector
        var publicKey:String
        runBlocking {
            publicKey= connector.receive()
        }
        println("Public key received.")
        symmetricKey = AesFactory.generateAESKey()
        println("Symmetric key generated.")
        val encryptedKey = RsaFactory.encryptWithPublicKey(symmetricKey, publicKey)
        runBlocking {
            connector.send(encryptedKey)
        }
        println("Symmetric key sent.")
    }

    fun send(message: String) {
        connector.send(AesFactory.encryptWithAESGCM(message, symmetricKey))
        println("Sent: $message")
    }

    fun receive(): String {
        val message = AesFactory.decryptWithAESGCM(connector.receive(), symmetricKey)
        println("Received: $message")
        return message
    }

    object RsaFactory {
        private const val KEY_SIZE = 4096 // Key size in bits

        fun generateRSAKeyPair(): Pair<String, String> {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(KEY_SIZE)
            val keyPair = keyPairGenerator.generateKeyPair()
            return Pair(
                encodePublicKeyToString(keyPair.public), encodePrivateKeyToString(keyPair.private)
            )
        }

        fun encryptWithPublicKey(data: String, publicKeyString: String): String {
            val publicKey = decodePublicKeyFromString(publicKeyString)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            return Base64.getEncoder().encodeToString(encryptedBytes)
        }

        fun decryptWithPrivateKey(encryptedData: String, privateKeyString: String): String {
            val privateKey = decodePrivateKeyFromString(privateKeyString)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val encryptedBytes = Base64.getDecoder().decode(encryptedData)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        }

        private fun encodePublicKeyToString(publicKey: PublicKey): String {
            return Base64.getEncoder().encodeToString(publicKey.encoded)
        }

        private fun decodePublicKeyFromString(publicKeyString: String): PublicKey {
            val keyBytes = Base64.getDecoder().decode(publicKeyString)
            val keyFactory = java.security.KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(java.security.spec.X509EncodedKeySpec(keyBytes))
        }

        private fun encodePrivateKeyToString(privateKey: PrivateKey): String {
            return Base64.getEncoder().encodeToString(privateKey.encoded)
        }

        private fun decodePrivateKeyFromString(privateKeyString: String): PrivateKey {
            val keyBytes = Base64.getDecoder().decode(privateKeyString)
            val keyFactory = java.security.KeyFactory.getInstance("RSA")
            return keyFactory.generatePrivate(java.security.spec.PKCS8EncodedKeySpec(keyBytes))
        }
    }

    object AesFactory {
        fun generateAESKey(): String {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val secretKey = keyGenerator.generateKey()
            return Base64.getEncoder().encodeToString(secretKey.encoded)
        }

        fun encryptWithAESGCM(data: String, key: String): String {
            val secretKey = decodeAESKeyFromString(key)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            val ivAndCiphertext = ByteArray(iv.size + encryptedBytes.size)
            iv.copyInto(ivAndCiphertext, 0)
            encryptedBytes.copyInto(ivAndCiphertext, iv.size)
            return Base64.getEncoder().encodeToString(ivAndCiphertext)
        }

        fun decryptWithAESGCM(ivAndCiphertext: String, key: String): String {
            val secretKey = decodeAESKeyFromString(key)
            val ivAndCiphertextBytes = Base64.getDecoder().decode(ivAndCiphertext)
            val iv = ByteArray(12)
            ivAndCiphertextBytes.copyInto(iv, 0, 0, 12)
            val encryptedBytes = ByteArray(ivAndCiphertextBytes.size - 12)
            ivAndCiphertextBytes.copyInto(encryptedBytes, 0, 12)
            val spec = GCMParameterSpec(128, iv)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
        }

        fun decodeAESKeyFromString(keyString: String): SecretKey {
            val keyBytes = Base64.getDecoder().decode(keyString)
            return SecretKeySpec(keyBytes, "AES")
        }
    }
}