package com.imr.example.newsmartykotlin.domain.model

data class SuitCategory(
    val id: String,
    val title: String,
    val items: List<SuitItem>
)