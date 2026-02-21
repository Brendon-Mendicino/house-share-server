package com.github.brendonmendicino.houseshareserver

import com.github.brendonmendicino.houseshareserver.configuration.OpenTelemetryConfiguration
import com.github.brendonmendicino.houseshareserver.properties.PaddleProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.core.task.support.ContextPropagatingTaskDecorator

@EnableConfigurationProperties(PaddleProperties::class)
@SpringBootApplication
@Import(OpenTelemetryConfiguration::class, ContextPropagatingTaskDecorator::class)
class HouseShareServerApplication

fun main(args: Array<String>) {
    runApplication<HouseShareServerApplication>(*args)
}
