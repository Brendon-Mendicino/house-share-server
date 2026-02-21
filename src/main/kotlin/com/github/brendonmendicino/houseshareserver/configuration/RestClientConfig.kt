package com.github.brendonmendicino.houseshareserver.configuration

import com.github.brendonmendicino.houseshareserver.service.PaddleService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient
import java.net.http.HttpClient

@Configuration
class RestClientConfig {
    /**
     * I forced the HTTP1/1 because the python server could not handle HTTP2 which is the default.
     */
    @Bean
    fun paddleService(builder: RestClient.Builder, @Value($$"${service.paddle.url}") paddleUrl: String): PaddleService {
        val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build()

        val restClient = builder
            .requestFactory(JdkClientHttpRequestFactory(httpClient))
            .baseUrl(paddleUrl)
            .build()

        val factory = HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()

        return factory.createClient<PaddleService>()
    }
}