package com.algorithmlearning.app

import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.auth.AuthSession
import com.algorithmlearning.shared.auth.AuthUser
import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ProblemQuery
import com.algorithmlearning.shared.library.ProblemRepository
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ProblemWrite
import com.algorithmlearning.shared.library.ReviewStatus
import com.algorithmlearning.shared.library.ReviewSummary
import com.algorithmlearning.shared.library.Solution
import com.algorithmlearning.shared.library.SolutionLanguage
import com.algorithmlearning.shared.library.SolutionRepository
import com.algorithmlearning.shared.library.SolutionWrite
import com.algorithmlearning.shared.library.Tag
import com.algorithmlearning.shared.library.TagRepository
import kotlin.time.Instant

val testInstant: Instant = Instant.parse("2026-01-01T00:00:00Z")

fun authSession(email: String): AuthSession =
    AuthSession(user = AuthUser(id = "user-1", email = email), accessToken = "token", expiresAt = testInstant)

fun problemSummary(
    id: String,
    title: String,
    difficulty: ProblemDifficulty = ProblemDifficulty.EASY,
    platform: ProblemPlatform = ProblemPlatform.LEETCODE,
    tags: List<Tag> = emptyList(),
): ProblemSummary = ProblemSummary(
    id = id,
    title = title,
    platform = platform,
    externalProblemId = null,
    difficulty = difficulty,
    tags = tags,
    review = ReviewSummary(
        status = ReviewStatus.NEVER_REVIEWED,
        confidence = null,
        lastReviewedAt = null,
        nextReviewAt = null,
        reviewCount = 0,
    ),
    createdAt = testInstant,
    updatedAt = testInstant,
)

fun problemDetail(
    id: String,
    title: String,
    notes: String? = null,
    tags: List<Tag> = emptyList(),
): ProblemDetail = ProblemDetail(
    summary = problemSummary(id = id, title = title, tags = tags),
    externalUrl = null,
    description = null,
    notes = notes,
    keyInsight = null,
    timeComplexity = null,
    spaceComplexity = null,
    mistakes = null,
    interviewNotes = null,
    solutions = emptyList(),
)

fun problemSolution(
    id: String,
    problemId: String,
    language: SolutionLanguage = SolutionLanguage.KOTLIN,
    code: String = "return 0",
    explanation: String? = null,
): Solution = Solution(
    id = id,
    problemId = problemId,
    language = language,
    code = code,
    explanation = explanation,
    createdAt = testInstant,
    updatedAt = testInstant,
)

class FakeAuthRepository(
    var loginHandler: suspend (String, String) -> AuthSession = { email, _ -> authSession(email) },
    var registerHandler: suspend (String, String) -> AuthSession = { email, _ -> authSession(email) },
    var restoreHandler: suspend () -> AuthSession? = { null },
) : AuthRepository {
    val loginCalls = mutableListOf<Pair<String, String>>()
    val registerCalls = mutableListOf<Pair<String, String>>()
    var logoutCount = 0
    var invalidateCount = 0

    override suspend fun login(email: String, password: String): AuthSession {
        loginCalls += email to password
        return loginHandler(email, password)
    }

    override suspend fun register(email: String, password: String): AuthSession {
        registerCalls += email to password
        return registerHandler(email, password)
    }

    override suspend fun restore(): AuthSession? = restoreHandler()

    override suspend fun logout() {
        logoutCount++
    }

    override suspend fun accessToken(): String? = null

    override suspend fun invalidateAccessToken() {
        invalidateCount++
    }
}

class FakeProblemRepository : ProblemRepository {
    val queries = mutableListOf<ProblemQuery>()
    var listHandler: suspend (ProblemQuery) -> Page<ProblemSummary> =
        { query -> Page(emptyList(), query.page, query.pageSize, 0, 1) }
    var getHandler: suspend (String) -> ProblemDetail = { error("no detail configured") }
    var createHandler: suspend (ProblemWrite) -> ProblemDetail = { error("no create configured") }
    var replaceHandler: suspend (String, ProblemWrite) -> ProblemDetail =
        { _, _ -> error("no replace configured") }
    var deleteHandler: suspend (String) -> Unit = {}
    val created = mutableListOf<ProblemWrite>()
    val replaced = mutableListOf<Pair<String, ProblemWrite>>()
    val deleted = mutableListOf<String>()

    override suspend fun list(query: ProblemQuery): Page<ProblemSummary> {
        queries += query
        return listHandler(query)
    }

    override suspend fun get(id: String): ProblemDetail = getHandler(id)

    override suspend fun create(write: ProblemWrite): ProblemDetail {
        created += write
        return createHandler(write)
    }

    override suspend fun replace(id: String, write: ProblemWrite): ProblemDetail {
        replaced += id to write
        return replaceHandler(id, write)
    }

    override suspend fun delete(id: String) {
        deleted += id
        deleteHandler(id)
    }
}

class FakeTagRepository : TagRepository {
    var listResult: List<Tag> = emptyList()
    var listHandler: suspend () -> List<Tag> = { listResult }
    var createHandler: suspend (String) -> Tag = { name -> Tag(id = "tag-$name", name = name) }
    val created = mutableListOf<String>()

    override suspend fun list(query: String?): List<Tag> = listHandler()

    override suspend fun create(name: String): Tag {
        created += name
        return createHandler(name)
    }
}

class FakeSolutionRepository : SolutionRepository {
    var listResult: List<Solution> = emptyList()
    var listHandler: suspend (String) -> List<Solution> = { listResult }
    var createHandler: suspend (String, SolutionWrite) -> Solution =
        { problemId, write -> problemSolution(id = "solution-new", problemId = problemId, code = write.code) }
    var deleteHandler: suspend (String) -> Unit = {}
    val created = mutableListOf<Pair<String, SolutionWrite>>()
    val deleted = mutableListOf<String>()

    override suspend fun list(problemId: String): List<Solution> = listHandler(problemId)

    override suspend fun create(problemId: String, write: SolutionWrite): Solution {
        created += problemId to write
        return createHandler(problemId, write)
    }

    override suspend fun replace(id: String, write: SolutionWrite): Solution =
        error("replace is not used by the presentation layer")

    override suspend fun delete(id: String) {
        deleted += id
        deleteHandler(id)
    }
}
