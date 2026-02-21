package com.github.brendonmendicino.houseshareserver.dto

data class OcrText(
    val text: String,
    val bbox: Bbox,
    val score: Double,
) {
    data class Bbox(
        val x1: Int,
        val y1: Int,
        val x2: Int,
        val y2: Int,
    )
}