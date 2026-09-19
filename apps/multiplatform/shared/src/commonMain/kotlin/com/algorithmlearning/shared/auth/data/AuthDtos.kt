package com.algorithmlearning.shared.auth.data

import kotlinx.serialization.Serializable

@Serializable
internal data class CredentialsDto(
    val email: String,
    val password: String,
)

@Serializable
internal data class UserDto(
    val id: String,
    val email: String,
)

@Serializable
internal data class AuthResponseDto(
    val user: UserDto,
    val accessToken: String,
    val expiresAt: String,
)

@Serializable
internal data class AccessTokenDto(
    val accessToken: String,
    val expiresAt: String,
)
