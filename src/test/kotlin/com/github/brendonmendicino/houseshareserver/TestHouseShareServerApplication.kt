package com.github.brendonmendicino.houseshareserver

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<HouseShareServerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
