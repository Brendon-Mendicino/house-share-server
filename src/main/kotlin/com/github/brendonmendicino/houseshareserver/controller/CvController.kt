package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.service.PaddleService
import com.github.brendonmendicino.houseshareserver.service.TesseractService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class CvController(
    private val tesseractService: TesseractService,
    private val paddleService: PaddleService,
) {
    companion object {
        val logger = LoggerFactory.getLogger(CvController::class.java)!!
    }

//    @PostMapping("/cv")
//    fun post(@RequestParam("file") file: MultipartFile): ResponseEntity<ByteArray> {
//        val processed = cvService.process(file)
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.IMAGE_JPEG)
//            .body(processed)
//    }

    @PostMapping("/cv", produces = ["application/json"])
    fun post(@RequestParam("file") file: MultipartFile) = paddleService.processImage(file)
}