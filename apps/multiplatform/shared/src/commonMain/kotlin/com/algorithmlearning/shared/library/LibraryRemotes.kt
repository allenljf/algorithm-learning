package com.algorithmlearning.shared.library

/**
 * Transport contracts for the frozen library API. Implementations adapt Ktor;
 * callers see only domain models and [ApiFailure].
 */
interface ProblemRemote {
    suspend fun list(query: ProblemQuery): Page<ProblemSummary>
    suspend fun get(id: String): ProblemDetail
    suspend fun create(write: ProblemWrite): ProblemDetail
    suspend fun replace(id: String, write: ProblemWrite): ProblemDetail
    suspend fun delete(id: String)
}

interface TagRemote {
    suspend fun list(query: String? = null): List<Tag>
    suspend fun create(name: String): Tag
}

interface SolutionRemote {
    suspend fun list(problemId: String): List<Solution>
    suspend fun create(problemId: String, write: SolutionWrite): Solution
    suspend fun replace(id: String, write: SolutionWrite): Solution
    suspend fun delete(id: String)
}

interface ReviewRemote {
    suspend fun submit(problemId: String, confidence: Int, notes: String?): Review

    /** Due problems, oldest due first. */
    suspend fun due(page: Int = 1, pageSize: Int = 20): List<ProblemSummary>

    /** Review history, newest first. */
    suspend fun history(problemId: String, page: Int = 1, pageSize: Int = 20): List<Review>
}

interface DashboardRemote {
    suspend fun get(): Dashboard
}
