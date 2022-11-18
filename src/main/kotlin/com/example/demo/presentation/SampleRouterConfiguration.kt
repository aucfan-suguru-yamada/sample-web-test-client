package com.example.demo.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.util.UriComponentsBuilder

@Configuration
class SampleRouterConfiguration {
    @Bean
    fun sampleRouter(sampleHandler: SampleHandler) = coRouter {
        GET("/api/sample", sampleHandler::sample)
    }
}

fun main() {
    val uri = UriComponentsBuilder.fromPath("/hotel+list/{city}")
        .queryParam("q", "{q}")
        .build("New York", "foo+bar")
    println(uri)
}
