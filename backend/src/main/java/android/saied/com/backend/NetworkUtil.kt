package android.saied.com.backend

import arrow.core.Try
import io.ktor.client.HttpClient
import io.ktor.client.request.get

suspend fun fetchHtml(url: String, httpClient: HttpClient): Try<String> =
    httpClient.use { client ->
        try {
            Try.just(client.get(url))
        } catch (exp: Exception) {
            Try.raise(exp)
        }
    }
