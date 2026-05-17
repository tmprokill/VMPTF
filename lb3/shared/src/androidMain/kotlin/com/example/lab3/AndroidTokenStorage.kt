package com.example.lab3

import android.content.Context
import com.example.lab3.data.TokenStorage

class AndroidTokenStorage(context: Context) : TokenStorage {
    private val prefs = context.getSharedPreferences("blog_prefs", Context.MODE_PRIVATE)

    override fun getToken(): String? = prefs.getString("jwt_token", null)

    override fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    override fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }
}
