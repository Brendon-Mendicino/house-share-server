package com.github.brendonmendicino.houseshareserver.dto

data class OcrResult(
    val lines: List<List<OcrText>>,
)