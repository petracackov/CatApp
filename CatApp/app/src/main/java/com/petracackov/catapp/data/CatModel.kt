package com.petracackov.catapp.data

data class CatModel(
    val id: String,
    val url: String,
    val categories: List<String>,
    val breeds: List<String>,
)