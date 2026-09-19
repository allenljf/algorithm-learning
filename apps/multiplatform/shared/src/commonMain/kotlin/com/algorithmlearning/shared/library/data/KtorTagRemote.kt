package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.Tag
import com.algorithmlearning.shared.library.TagRemote
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorTagRemote(private val api: ApiClient) : TagRemote {

    override suspend fun list(query: String?): List<Tag> = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/tags") {
                bearer(token)
                query?.takeIf { it.isNotBlank() }?.let { parameter("q", it) }
            }
        }.body<List<TagDto>>().map { it.toDomain() }
    }

    override suspend fun create(name: String): Tag = api.decode {
        api.authorized { token ->
            api.client.post("${api.root}/tags") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(TagCreateDto(name))
            }
        }.body<TagDto>().toDomain()
    }
}
