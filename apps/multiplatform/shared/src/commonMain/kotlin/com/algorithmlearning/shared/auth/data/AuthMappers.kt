package com.algorithmlearning.shared.auth.data

import com.algorithmlearning.shared.auth.AccessToken
import com.algorithmlearning.shared.auth.AuthSession
import com.algorithmlearning.shared.auth.AuthUser
import kotlin.time.Instant

internal fun UserDto.toDomain(): AuthUser = AuthUser(id = id, email = email)

internal fun AuthResponseDto.toDomain(): AuthSession = AuthSession(
    user = user.toDomain(),
    accessToken = accessToken,
    expiresAt = Instant.parse(expiresAt),
)

internal fun AccessTokenDto.toDomain(): AccessToken = AccessToken(
    value = accessToken,
    expiresAt = Instant.parse(expiresAt),
)
