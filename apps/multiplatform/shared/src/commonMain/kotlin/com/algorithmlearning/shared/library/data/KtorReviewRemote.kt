package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.Review
import com.algorithmlearning.shared.library.ReviewRemote
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorReviewRemote(private val api: ApiClient) : ReviewRemote {

    override suspend fun submit(problemId: String, confidence: Int, notes: String?): Review = api.decode {
        api.authorized { token ->
            api.client.post("${api.root}/reviews") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(ReviewWriteDto(problemId = problemId, confidence = confidence, notes = notes))
            }
        }.body<ReviewDto>().toDomain()
    }

    override suspend fun due(page: Int, pageSize: Int): List<ProblemSummary> = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/reviews/today") {
                bearer(token)
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
        }.body<List<ProblemSummaryDto>>().map { it.toDomain() }
    }

    override suspend fun history(problemId: String, page: Int, pageSize: Int): List<Review> = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/reviews/history") {
                bearer(token)
                parameter("problemId", problemId)
                parameter("page", page)
                parameter("pageSize", pageSize)
            }
        }.body<List<ReviewDto>>().map { it.toDomain() }
    }
}
