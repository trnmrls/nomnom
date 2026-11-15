package com.example.nomnom

data class FoodEntry(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val rating: Float,
    val photoUri: String? = null
)
