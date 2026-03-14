package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.dto.OcrText
import com.github.brendonmendicino.houseshareserver.service.PaddleService
import com.github.brendonmendicino.houseshareserver.service.TesseractService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import kotlin.math.pow
import kotlin.math.sqrt

@RestController
class CvController(
    private val tesseractService: TesseractService,
    private val paddleService: PaddleService,
) {
    companion object {
        val logger = LoggerFactory.getLogger(CvController::class.java)!!
    }

    fun parsePrice(text: String): Int? {
        val priceRegex = Regex("""\b\d(?:[,.']+\d)*[,.]\d{2}\b""")
        val match = priceRegex.find(text)?.value ?: return null

        val normPrice = match
            .replace(",", "")
            .replace(".", "")
            .replace("'", "")
            .toIntOrNull()

        return normPrice
    }

    @PostMapping("/cv", produces = ["application/json"])
    fun post(@RequestParam("file") file: MultipartFile): Any {
        val ocrResult = paddleService.processImage(file)

        val ocr = ocrResult.lines.flatten()

        fun Iterable<Int>.std(): Double {
            val nu = average()
            val sum = map { (it - nu).pow(2) }.average()
            return sqrt(sum)
        }

        val stdH = ocr.map { it.bbox.height }.std()
        val thresh = stdH / 2

        // Form the clusters
        val clusters = mutableListOf(mutableListOf<OcrText>())
        var prevY = Double.MIN_VALUE

        for (text in ocr.sortedBy { it.bbox.y }) {
            if (text.bbox.y - prevY <= thresh) {
                clusters.last().add(text)
            } else {
                clusters.add(mutableListOf(text))
                prevY = text.bbox.y
            }
        }

        val lines = clusters.map { line ->
            line.sortedBy { it.bbox.x1 }.map { it.text }
        }

        lines.zipWithNext { line, next ->
            val line = line.toMutableList()

            line.removeAll {
                val price = parsePrice(it)
                if (price != null) {

                }
            }
        }

        return Any()
    }
}