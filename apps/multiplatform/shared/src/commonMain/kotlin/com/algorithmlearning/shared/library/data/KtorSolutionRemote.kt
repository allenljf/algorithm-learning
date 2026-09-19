package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.Solution
import com.algorithmlearning.shared.library.SolutionRemote
import com.algorithmlearning.shared.library.SolutionWrite
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorSolutionRemote(private val api: ApiClient) : SolutionRemote {

    override suspend fun list(problemId: String): List<Solution> = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/problems/$problemId/solutions") { bearer(token) }
        }.body<List<SolutionDto>>().map { it.toDomain() }
    }

    override suspend fun create(problemId: String, write: SolutionWrite): Solution = api.decode {
        api.authorized { token ->
            api.client.post("${api.root}/problems/$problemId/solutions") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(write.toDto())
            }
        }.body<SolutionDto>().toDomain()
    }

    override suspend fun replace(id: String, write: SolutionWrite): Solution = api.decode {
        api.authorized { token ->
            api.client.put("${api.root}/solutions/$id") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(write.toDto())
            }
        }.body<SolutionDto>().toDomain()
    }

    override suspend fun delete(id: String) {
        api.decode {
            api.authorized { token ->
                api.client.delete("${api.root}/solutions/$id") { bearer(token) }
            }
        }
    }
}
