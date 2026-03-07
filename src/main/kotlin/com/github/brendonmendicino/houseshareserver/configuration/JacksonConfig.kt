package com.github.brendonmendicino.houseshareserver.configuration

import org.springframework.ai.ollama.api.ThinkOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.JacksonModule
import tools.jackson.databind.module.SimpleModule

@Configuration
class JacksonConfig {
    @Bean
    fun thinkOptionModule(): JacksonModule {
        val module = SimpleModule()
        module.addSerializer(ThinkOption::class.java, ChatClientConfig.ThinkOptionSerializer())
        module.addSerializer(ThinkOption.ThinkBoolean::class.java, ChatClientConfig.ThinkOptionSerializer())
        module.addSerializer(ThinkOption.ThinkLevel::class.java, ChatClientConfig.ThinkOptionSerializer())
        return module
    }
}