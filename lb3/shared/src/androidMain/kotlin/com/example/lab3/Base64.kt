package com.example.lab3

import android.util.Base64

actual fun decodeBase64(input: String): String {
    return Base64.decode(input, Base64.DEFAULT).decodeToString()
}
