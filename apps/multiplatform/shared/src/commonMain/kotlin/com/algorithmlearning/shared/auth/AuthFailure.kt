package com.algorithmlearning.shared.auth

/**
 * The typed failures the auth data layer surfaces to callers. Raw HTTP status
 * codes and transport exceptions never leak past this boundary.
 */
enum class AuthFailureKind {
    /** `401` from login/register: the credentials do not match. */
    INVALID_CREDENTIALS,

    /** `401`/`403` from refresh, me, or logout: the session is no longer valid. */
    UNAUTHORIZED,

    /** `429`: the caller exceeded the auth rate limit. */
    RATE_LIMITED,

    /** The request never produced an HTTP response. */
    NETWORK,

    /** `5xx`: the API failed unexpectedly. */
    SERVER,

    /** Any other status or a malformed response. */
    UNEXPECTED,
}

class AuthFailure(
    val kind: AuthFailureKind,
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message ?: kind.name, cause)
