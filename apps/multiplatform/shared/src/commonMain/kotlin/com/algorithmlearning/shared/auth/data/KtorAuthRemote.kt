package com.algorithmlearning.shared.auth.data

import com.algorithmlearning.shared.auth.AccessToken
import com.algorithmlearning.shared.auth.AuthFailure
import com.algorithmlearning.shared.auth.AuthFailureKind
import com.algorithmlearning.shared.auth.AuthRemote
import com.algorithmlearning.shared.auth.AuthSession
import com.algorithmlearning.shared.auth.AuthUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

/**
 * Ktor implementation of [AuthRemote] for the frozen `/api/v1/auth` contract.
 * The refresh cookie is handled by the injected [HttpClient]; this class only
 * moves access tokens and domain models.
 */
class KtorAuthRemote(
    private val client: HttpClient,
    baseUrl: String,
) : AuthRemote {

    private val root: String = baseUrl.trimEnd('/') + "/api/v1/auth"

    override suspend fun register(email: String, password: String): AuthSession = request {
        client.post("$root/register") {
            contentType(ContentType.Application.Json)
            setBody(CredentialsDto(email, password))
        }.decode(AuthFailureKind.INVALID_CREDENTIALS).body<AuthResponseDto>().toDomain()
    }

    override suspend fun login(email: String, password: String): AuthSession = request {
        client.post("$root/login") {
            contentType(ContentType.Application.Json)
            setBody(CredentialsDto(email, password))
        }.decode(AuthFailureKind.INVALID_CREDENTIALS).body<AuthResponseDto>().toDomain()
    }

    override suspend fun refresh(): AccessToken = request {
        client.post("$root/refresh")
            .decode(AuthFailureKind.UNAUTHORIZED)
            .body<AccessTokenDto>()
            .toDomain()
    }

    override suspend fun currentUser(accessToken: String): AuthUser = request {
        client.get("$root/me") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.decode(AuthFailureKind.UNAUTHORIZED).body<UserDto>().toDomain()
    }

    override suspend fun logout() {
        request {
            client.post("$root/logout").decode(AuthFailureKind.UNAUTHORIZED)
        }
    }

    private suspend fun <T> request(block: suspend () -> T): T = try {
        block()
    } catch (failure: AuthFailure) {
        throw failure
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        throw AuthFailure(AuthFailureKind.UNEXPECTED, error.message, error)
    }

    private fun HttpResponse.decode(unauthorized: AuthFailureKind): HttpResponse = when {
        status.isSuccess() -> this
        status == HttpStatusCode.Unauthorized -> throw AuthFailure(unauthorized)
        status == HttpStatusCode.Forbidden -> throw AuthFailure(unauthorized)
        status == HttpStatusCode.TooManyRequests -> throw AuthFailure(AuthFailureKind.RATE_LIMITED)
        status.value >= 500 -> throw AuthFailure(AuthFailureKind.SERVER, "HTTP ${status.value}")
        else -> throw AuthFailure(AuthFailureKind.UNEXPECTED, "HTTP ${status.value}")
    }
}
