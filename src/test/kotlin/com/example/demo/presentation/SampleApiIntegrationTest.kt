package com.example.demo.presentation

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import java.net.URI

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
internal class SampleApiIntegrationTest @Autowired constructor(
    private val webTestClient: WebTestClient
) {

    @Test
    @DisplayName("クエリパラメータに文字列を指定して、レスポンスの返却に成功する")
    fun getSampleTest() {
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    .queryParam("requestQuery", "テスト")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("テスト")
    }

    @Test
    @DisplayName("クエリパラメータにISO時刻を指定して、レスポンスの返却に成功する??")
    // このテストは失敗します。
    fun getSampleByDateTimeFormatTest() {
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    // クエリパラメータにISO時刻をセット
                    .queryParam("requestQuery", "2022-11-20T00:00:00+09:00")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("2022-11-20T00:00:00+09:00")
    }

    @Test
    @DisplayName("クエリパラメータにURIエンコードしたISO時刻を指定して、レスポンスの返却に成功する??")
    fun getSampleByEncodedDateTimeFormatTest() {
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    // クエリパラメータにURIエンコードされたISO時刻をセット
                    .queryParam("requestQuery", "2022-11-20T00:00:00%2B09:00")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("2022-11-20T00:00:00+09:00")
    }

    @Test
    @DisplayName("クエリパラメータのISO時刻をテンプレート変数として指定して、レスポンスの返却に成功する")
    fun getSampleByDateTimeFormatInUriTemplateTest() {
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    // クエリパラメータにURIエンコードされたISO時刻をセット
                    .queryParam("requestQuery", "{requestQuery}")
                    .build("2022-11-20T00:00:00+09:00")
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("2022-11-20T00:00:00+09:00")
    }

    @Test
    @DisplayName("クエリパラメータのISO時刻をテンプレート変数として指定して、レスポンスの返却に成功する")
    // このテストはハンドラークラスが対応しないので失敗します。
    fun getSampleByDateTimeFormatsInUriTemplateTest() {
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    // URIテンプレート変数を複数使用してみる
                    .queryParam("startDateTIme", "{startDateTIme}")
                    .queryParam("endDateTIme", "{endDateTIme}")
                    .build(
                        // URIテンプレート変数を置き換えたい値をMapで渡す
                        mapOf(
                            "startDateTIme" to "2022-11-20T00:00:00+09:00",
                            "endDateTIme" to "2022-11-30T00:00:00+09:00"
                        )
                    )
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("2022-11-20T00:00:00+09:00")
    }

    // 独自の処理を追加したcustomWebTestClientを追加
    val customWebTestClient = webTestClient.mutate().filter(filterPlusSignEncoding()).build()

    private fun filterPlusSignEncoding(): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest, nextFilter: ExchangeFunction ->
            // リクエストのURIに含まれる"+"を"%2B"へ置き換える
            val encodedUrl = StringUtils.replace(clientRequest.url().toString(), "+", "%2B")
            val filteredRequest = ClientRequest.from(clientRequest)
                .url(URI.create(encodedUrl))
                .build()
            nextFilter.exchange(filteredRequest)
        }
    }

    @Test
    @DisplayName("クエリパラメータにISO時刻を指定して、レスポンスの返却に成功する")
    fun getSampleByDateTimeFormatByCustomWebClientTest() {
        customWebTestClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/sample")
                    // クエリパラメータにISO時刻をセット
                    .queryParam("requestQuery", "2022-11-20T00:00:00+09:00")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.receivedQuery").isEqualTo("2022-11-20T00:00:00+09:00")
    }
}
