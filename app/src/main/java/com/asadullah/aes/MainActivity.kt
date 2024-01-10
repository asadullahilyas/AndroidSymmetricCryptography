package com.asadullah.aes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.asadullah.aes.ui.theme.AESTheme
import com.asadullah.aes_library.AES
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aes = AES()
        val iv = aes.generateRandomIV()

        val secretKey = aes.generateSecretKey()

        val decryptedFile = File(filesDir, "Decrypted.mp3")
        if (decryptedFile.exists().not()) {

            val internalStoragePlainTestFile = File(filesDir, "original.mp3")

            if (internalStoragePlainTestFile.exists().not()) {
                val assetInputStream = assets.open("original.mp3")
                assetInputStream.copyTo(internalStoragePlainTestFile.outputStream())
            }

            val encryptedFile = aes.encryptFile(secretKey, iv, internalStoragePlainTestFile)
            aes.decryptFile(secretKey, iv, encryptedFile, decryptedFile)
        }

//        aes.generateAndSaveKeyInKeyStore()

        println("ENCRYPTION/DECRYPTION COMPLETED")

        setContent {
            AESTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AESTheme {
        Greeting("Android")
    }
}