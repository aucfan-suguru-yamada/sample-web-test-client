package com.example.demo.presentation

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class SampleHandler {

    suspend fun sample(request: ServerRequest): ServerResponse {
        val queryParam = request.queryParam("requestQuery").get()
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                """{
                    "receivedQuery": "$queryParam"
                }""".trimIndent()
            )
    }
}
