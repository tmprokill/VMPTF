package com.example.lab3.data

object AppDependencies {
    lateinit var tokenStorage: TokenStorage
    lateinit var apiService: ApiService

    fun initialize(tokenStorage: TokenStorage) {
        this.tokenStorage = tokenStorage
        this.apiService = ApiService(tokenStorage)
    }
}
