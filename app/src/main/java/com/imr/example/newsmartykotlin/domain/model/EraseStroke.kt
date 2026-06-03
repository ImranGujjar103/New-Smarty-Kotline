package com.imr.example.newsmartykotlin.domain.model

data class ErasePoint(
    val x: Float,
    val y: Float
)

data class EraseStroke(
    val points: List<ErasePoint>,
    val brushSize: Float
)