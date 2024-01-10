package com.asadullah.aes_library

import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AES {

    private val cipherTransformation = "AES/CBC/PKCS7Padding"

    /*private val iv = generateRandomIV()

    fun generateAndSaveKeyInKeyStore() {
        // Generate a secret key
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder("MySecretKey", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false) // Adjust as needed
            .setKeySize(256)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        val secretKey = keyGenerator.generateKey()

        val testString = "This is my test string"
        encryptString(secretKey, iv, testString).also { println(it) }
        encryptString(secretKey, iv, testString).also { println(it) }
        encryptString(secretKey, iv, testString).also { println(it) }

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val secretKeyEntry = keyStore.getEntry("MySecretKey", null)
        val retrievedSecretKey = (secretKeyEntry as KeyStore.SecretKeyEntry).secretKey
    }*/

    private fun convertKeyToString(secretKey: SecretKey): String {
        return Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
    }

    private fun convertStringToKey(encodedKey: String): SecretKey {
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    fun generateSecretKey(): String {
        val secureRandom = SecureRandom()
        val keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
        keyGen.init(256, secureRandom)
        return convertKeyToString(keyGen.generateKey())
    }

    fun generateRandomIV(): String {
        val random = SecureRandom()
        val generated: ByteArray = random.generateSeed(16)
        return Base64.encodeToString(generated, Base64.DEFAULT)
    }

    fun encryptString(secretKey: String, iv: String, text: String): String {
        val realSecretKey = convertStringToKey(secretKey)
        val cipher = Cipher.getInstance(cipherTransformation)
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.ENCRYPT_MODE, realSecretKey, ivSpec)
        val encryptedByteArray = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedByteArray, Base64.DEFAULT)
    }

    fun decryptString(secretKey: String, iv: String, encryptedText: String): String {
        val realSecretKey = convertStringToKey(secretKey)
        val cipher = Cipher.getInstance(cipherTransformation)
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, realSecretKey, ivSpec)
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val plainTextByteArray = cipher.doFinal(encryptedBytes)
        return String(plainTextByteArray, Charsets.UTF_8)
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class)
    fun encryptFile(secretKey: String, iv: String, file: File): File {

        val realSecretKey = convertStringToKey(secretKey)

        val encryptedFile = File(file.parentFile, "${file.name}.crypt")

        val fis = FileInputStream(file)
        val fos = FileOutputStream(encryptedFile)

        val cipher = Cipher.getInstance(cipherTransformation)
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.ENCRYPT_MODE, realSecretKey, ivSpec)

        val cos = CipherOutputStream(fos, cipher)
        var b: Int
        val d = ByteArray(1024)
        while (fis.read(d).also { b = it } != -1) {
            cos.write(d, 0, b)
        }
        cos.flush()
        cos.close()
        fis.close()

        return encryptedFile
    }

    @Throws(IOException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class)
    fun decryptFile(secretKey: String, iv: String, encryptedFile: File, outputFile: File) {

        val realSecretKey = convertStringToKey(secretKey)

        val fis = FileInputStream(encryptedFile)
        val fos = FileOutputStream(outputFile)

        val cipher = Cipher.getInstance(cipherTransformation)
        val ivSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, realSecretKey, ivSpec)

        val cis = CipherInputStream(fis, cipher)
        var b: Int
        val d = ByteArray(1024)
        while (cis.read(d).also { b = it } != -1) {
            fos.write(d, 0, b)
        }
        fos.flush()
        fos.close()
        cis.close()
    }
}