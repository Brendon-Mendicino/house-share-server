package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.service.PaddleService
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.multipart.MultipartFile

@RestController
class CvController(
    private val paddleService: PaddleService,
    private val chatClient: ChatClient,
    private val restClientBuilder: RestClient.Builder,
) {
    companion object {
        val logger = LoggerFactory.getLogger(CvController::class.java)!!

//        You are a receipt parser. Below is a JSON array of OCR lines extracted from a receipt image. Each object has a "text" field (the recognized text) and a "bbox" field [x1, y1, x2, y2] representing its position on the page (y increases downward).
//
//        OCR LINES:
//        %s
//
//        Extract the data and return ONLY a valid JSON object with this structure:
//        {
//            "company": "<store name or null>",
//            "items": [{"name": "<item name>", "price": <float>}],
//            "total": <float or null>
//        }
//
//        Rules:
//        - Use the "bbox" y-coordinate to infer reading order (lower y = higher on page)
//        - "price" and "total" must be numeric floats, never strings
//        - If a value cannot be determined, use null
//        - Do NOT include explanations, markdown, or any text outside the JSON


//        {
//            "items": [
//            { "name": "string", "price": number }
//            ],
//            "total": number,
//            "company": "string"
//        }
//
//        Input is a JSON array where each element contains:
//        {
//            "text": string,
//            "bbox": [x1, y1, x2, y2]
//        }
//
//        Rules:
//        - Extract purchased items with their prices.
//        - Ignore quantities, taxes, or metadata unless part of the item price.
//        - "total" must be the final receipt total.
//        - "company" must be the store/company name.
//        - Output MUST be valid JSON.
//        - Do not include explanations or markdown.

        val PROMPT_TEMPLATE: String = """
            You are an AI that extracts structured data from OCR results of receipts.

            OCR INPUT:
            %s
            """.trimIndent()

        val PROMPT_BRUTTO: String = $$"""
            You are an AI that extracts structured data from OCR results of receipts.
            
            OCR INPUT:
            %s
            Your response should be in JSON format.
            Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
            Do not include markdown code blocks in your response.
            Remove the ```json markdown from the output.
            Here is the JSON Schema instance your output must adhere to:
            ```{
              "$schema" : "https://json-schema.org/draft/2020-12/schema",
              "type" : "array",
              "items" : {
                "type" : "object",
                "properties" : {
                  "name" : {
                    "type" : "string"
                  },
                  "price" : {
                    "type" : "number"
                  }
                },
                "required" : [ "name", "price" ],
                "additionalProperties" : false
              }
            }```
        """.trimIndent()
    }

//    @PostMapping("/cv")
//    fun post(@RequestParam("file") file: MultipartFile): ResponseEntity<ByteArray> {
//        val processed = cvService.process(file)
//
//        return ResponseEntity.ok()
//            .contentType(MediaType.IMAGE_JPEG)
//            .body(processed)
//    }

    data class Receipt(
        val name: String,
        val price: Double,
    )

    val client = restClientBuilder.baseUrl("http://localhost:11434")
        .build()


//    @PostMapping("/cv", produces = ["application/json"])
//    fun post(@RequestParam("file") file: MultipartFile) = paddleService.processImage(file)

    @PostMapping("/cv")
    fun test(@RequestParam("file") file: MultipartFile): Any? {
        val output = paddleService.processImage(file);

        restClientBuilder

//        val ocr = output.lines
//            .flatten().joinToString(separator = ",", prefix = "[", postfix = "]") {
//                """{"text":"${
//                    it.text.replace(
//                        "\"",
//                        "'"
//                    )
//                }","bbox":[${it.bbox.x1},${it.bbox.y1},${it.bbox.x2},${it.bbox.y2}]}"""
//            }
        val ocr = output.lines.flatten()
            .joinToString(prefix = "```\n", postfix = "\n```") { it.text }

        logger.warn(ocr)

        //Writing [ChatRequest[model=qwen3.5:0.8b, messages=[Message[role=USER, content=You are an AI that extracts structured data from OCR results of receipts.
        //
        //OCR INPUT:
        //```
        //OGA112027-084521748.130, 2016, SCONTRINO SENZA BISFENOL, OUNKONLINE, NSUEOAO'OnOIB OzAOS, pitunoddound o aed ououmdio, Op BuMi9zuOOA au, ezuejoin p ouozentis eun tnin es, 15512202135/2022-9C, Gaming, 1, contormita, market, CARREFOUR, MARKET, GS SPA, Via Tripoii, 12, Torino (To), P.IVA, 12683790153, te1. 011 393839, DOCUMENTO COMMERCIALE, di vendita o prestaziore, DESCRIZIONE, PREZZ0, %Iva, CREMA ARACHIDI 100%, 3.99, 10,00, CROSTINO NORMALE SAN, 2,29, 04,00, PANE GRATTUGIATO, 2.19, 04,00, CAROTE SF, 0, .94, 04.00, SAC COMPOSTABILE, 0, 02, 22.00, COL.DIFESA DENTI/GEN, 5, .29, 22,00, FUNGHI CHAMPIGNON AF, 2, ,28, 04,00, FAGE, TOTAL, 0%, 3, .59, 10,00, CAMOMILLA SOLUB MELA, 10.00, SOTTILI DI PETTO DI, 6,88, 10,00, COMPLESSIVO, TOTALEC, 30,46, DI CUI IVA, 2,85, BP ELETTRONICO, 24,00, EDENRED SMART, Ncarta 0000000000820821, 24,00, Euro, ES:407210, TML:00498903, N.OP:, da 000009, a, 000011, MATR:00820821, CIRC:, 006046, Buoni precedenti:, Scadenza 12/26, 017 buoni da E008,00, TOTALE, E136,00, Buoni utilizzati:, 003 buoni da E008,00, TOTALE E024,00, Saldo residuo scad 12/26, 014 buoni da E008,00, TOTALE E112,00, 200827791750
        //```
        //Your response should be in JSON format.
        //Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
        //Do not include markdown code blocks in your response.
        //Remove the ```json markdown from the output.
        //Here is the JSON Schema instance your output must adhere to:
        //```{
        //  "$schema" : "https://json-schema.org/draft/2020-12/schema",
        //  "type" : "array",
        //  "items" : {
        //    "type" : "object",
        //    "properties" : {
        //      "name" : {
        //        "type" : "string"
        //      },
        //      "price" : {
        //        "type" : "number"
        //      }
        //    },
        //    "required" : [ "name", "price" ],
        //    "additionalProperties" : false
        //  }
        //}```
        //, images=null, toolCalls=null, toolName=null, thinking=null]], stream=false, format=null, keepAlive=null, tools=[], options={top_p=1.0, think=false, min_p=0.0, presence_penalty=2.0, top_k=20, temperature=1.0, repeat_penalty=1.0}, think=ThinkBoolean[enabled=false]]] as "application/json" with org.springframework.http.converter.json.JacksonJsonHttpMessageConverter

        val res = client
            .post()
            .uri("/api/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                mapOf(
                    "model" to "qwen3.5:2b",
                    "messages" to listOf(
                        mapOf(
                            "role" to "USER",
                            "content" to String.format(PROMPT_BRUTTO, ocr),
                        ),
                    ),
                    "options" to mapOf(
                        "top_p" to 1.0,
                        "think" to false,
                        "min_p" to 0.0,
                        "presence_penalty" to 2.0,
                        "top_k" to 20,
                        "temperature" to 1.0,
                        "repeat_penalty" to 1.0,
                    ),
                    "stream" to false,
                    "think" to false,
                )
            )
            .retrieve()
            .body<Any>()

        // TODO: mannagia alla puttana di quelli di quelli springAI
//        val res = chatClient.prompt()
//            .user(String.format(PROMPT_TEMPLATE, ocr))
//            .call()
//            .entity<List<Receipt>>()

        logger.warn(res.toString())
        return res ?: "null"
    }
}