package com.algorithmlearning.shared.library

/**
 * Application-facing contracts. Screens depend on these; the remote adapters are
 * injected at the composition root and can be replaced with fakes in tests.
 */
interface ProblemRepository {
    suspend fun list(query: ProblemQuery): Page<ProblemSummary>
    suspend fun get(id: String): ProblemDetail
    suspend fun create(write: ProblemWrite): ProblemDetail
    suspend fun replace(id: String, write: ProblemWrite): ProblemDetail
    suspend fun delete(id: String)
}

interface TagRepository {
    suspend fun list(query: String? = null): List<Tag>
    suspend fun create(name: String): Tag
}

interface SolutionRepository {
    suspend fun list(problemId: String): List<Solution>
    suspend fun create(problemId: String, write: SolutionWrite): Solution
    suspend fun replace(id: String, write: SolutionWrite): Solution
    suspend fun delete(id: String)
}

interface ReviewRepository {
    suspend fun submit(problemId: String, confidence: Int, notes: String?): Review
    suspend fun due(page: Int = 1, pageSize: Int = 20): List<ProblemSummary>
    suspend fun history(problemId: String, page: Int = 1, pageSize: Int = 20): List<Review>
}

interface DashboardRepository {
    suspend fun get(): Dashboard
}

class RemoteProblemRepository(private val remote: ProblemRemote) : ProblemRepository {
    override suspend fun list(query: ProblemQuery): Page<ProblemSummary> = remote.list(query)
    override suspend fun get(id: String): ProblemDetail = remote.get(id)
    override suspend fun create(write: ProblemWrite): ProblemDetail = remote.create(write)
    override suspend fun replace(id: String, write: ProblemWrite): ProblemDetail = remote.replace(id, write)
    override suspend fun delete(id: String) = remote.delete(id)
}

class RemoteTagRepository(private val remote: TagRemote) : TagRepository {
    override suspend fun list(query: String?): List<Tag> = remote.list(query)
    override suspend fun create(name: String): Tag = remote.create(name)
}

class RemoteSolutionRepository(private val remote: SolutionRemote) : SolutionRepository {
    override suspend fun list(problemId: String): List<Solution> = remote.list(problemId)
    override suspend fun create(problemId: String, write: SolutionWrite): Solution = remote.create(problemId, write)
    override suspend fun replace(id: String, write: SolutionWrite): Solution = remote.replace(id, write)
    override suspend fun delete(id: String) = remote.delete(id)
}

class RemoteReviewRepository(private val remote: ReviewRemote) : ReviewRepository {
    override suspend fun submit(problemId: String, confidence: Int, notes: String?): Review =
        remote.submit(problemId, confidence, notes)
    override suspend fun due(page: Int, pageSize: Int): List<ProblemSummary> = remote.due(page, pageSize)
    override suspend fun history(problemId: String, page: Int, pageSize: Int): List<Review> =
        remote.history(problemId, page, pageSize)
}

class RemoteDashboardRepository(private val remote: DashboardRemote) : DashboardRepository {
    override suspend fun get(): Dashboard = remote.get()
}
