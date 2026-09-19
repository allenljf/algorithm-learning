package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemQuery
import com.algorithmlearning.shared.library.ProblemRemote
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ProblemWrite
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorProblemRemote(private val api: ApiClient) : ProblemRemote {

    override suspend fun list(query: ProblemQuery): Page<ProblemSummary> = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/problems") {
                bearer(token)
                query.query?.takeIf { it.isNotBlank() }?.let { parameter("q", it) }
                query.difficulty?.let { parameter("difficulty", it.wire) }
                query.platform?.let { parameter("platform", it.wire) }
                query.tagIds.forEach { parameter("tagId", it) }
                query.reviewStatus?.let { parameter("reviewStatus", it.wire) }
                parameter("sort", query.sort.wire)
                parameter("page", query.page)
                parameter("pageSize", query.pageSize)
            }
        }.body<PageDto<ProblemSummaryDto>>().toDomain { it.toDomain() }
    }

    override suspend fun get(id: String): ProblemDetail = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/problems/$id") { bearer(token) }
        }.body<ProblemDetailDto>().toDomain()
    }

    override suspend fun create(write: ProblemWrite): ProblemDetail = api.decode {
        api.authorized { token ->
            api.client.post("${api.root}/problems") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(write.toDto())
            }
        }.body<ProblemDetailDto>().toDomain()
    }

    override suspend fun replace(id: String, write: ProblemWrite): ProblemDetail = api.decode {
        api.authorized { token ->
            api.client.put("${api.root}/problems/$id") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(write.toDto())
            }
        }.body<ProblemDetailDto>().toDomain()
    }

    override suspend fun delete(id: String) {
        api.decode {
            api.authorized { token ->
                api.client.delete("${api.root}/problems/$id") { bearer(token) }
            }
        }
    }
}
