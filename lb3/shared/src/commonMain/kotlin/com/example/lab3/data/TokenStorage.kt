package com.example.lab3.data

interface TokenStorage {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
