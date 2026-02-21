package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.OcrResult
import io.micrometer.observation.annotation.Observed
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange
interface PaddleService {
    @Observed(name = "ocr.process")
    @PostExchange("/ocr", accept = [MediaType.APPLICATION_JSON_VALUE])
    fun processImage(file: MultipartFile): OcrResult
}