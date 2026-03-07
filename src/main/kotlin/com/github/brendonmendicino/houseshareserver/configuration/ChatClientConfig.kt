package com.github.brendonmendicino.houseshareserver.configuration

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.ThinkOption
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

@Configuration
class ChatClientConfig {
    @Bean
    fun ollamaChatClient(chatModel: OllamaChatModel): ChatClient {
        return ChatClient.create(chatModel)
    }

    @Bean
    @ConfigurationPropertiesBinding
    fun thinkOptionDeserialize(): Converter<String, ThinkOption?> = Converter { source ->
        when (source) {
            "true" -> ThinkOption.ThinkBoolean.ENABLED
            "false" -> ThinkOption.ThinkBoolean.DISABLED
            else -> ThinkOption.ThinkLevel(source)
        }
    }

//    @Bean
//    fun thinkOptionSerialize(): Converter<ThinkOption, String?> = Converter { source ->
//        when (val value = source.toJsonValue()) {
//            is Boolean -> value.toString()
//            is String -> "\"$value\""
//            else -> value.toString()
//        }
//    }

    class ThinkOptionSerializer : ValueSerializer<ThinkOption?>() {
        override fun serialize(
            value: ThinkOption?,
            gen: JsonGenerator?,
            ctxt: SerializationContext?
        ) {
            if (value == null) {
                gen!!.writeNull()
            } else {
                gen!!.writePOJO(value.toJsonValue())
            }
        }
    }
}