package com.algorithmlearning.shared.library

/**
 * The typed failure the library data layer surfaces. HTTP status codes,
 * `application/problem+json` bodies, and transport exceptions stop here.
 */
enum class ApiFailureKind {
    UNAUTHORIZED,
    NOT_FOUND,
    VALIDATION,
    CONFLICT,
    RATE_LIMITED,
    NETWORK,
    SERVER,
    UNEXPECTED,
}

class ApiFailure(
    val kind: ApiFailureKind,
    val statusCode: Int? = null,
    val code: String? = null,
    message: String? = null,
    val fieldErrors: Map<String, List<String>> = emptyMap(),
    cause: Throwable? = null,
) : Exception(message ?: code ?: kind.name, cause)
