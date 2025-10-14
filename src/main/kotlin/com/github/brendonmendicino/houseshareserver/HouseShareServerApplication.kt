package com.github.brendonmendicino.houseshareserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HouseShareServerApplication

fun main(args: Array<String>) {
    runApplication<HouseShareServerApplication>(*args)
}
