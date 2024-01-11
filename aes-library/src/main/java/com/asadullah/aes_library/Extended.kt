package com.asadullah.aes_library

import java.util.Base64

fun ByteArray.encodeToString(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun String.decodeToByteArray(): ByteArray {
    return Base64.getDecoder().decode(this)
}