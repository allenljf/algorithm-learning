package com.algorithmlearning.app.acceptance

import com.algorithmlearning.app.testInstant
import com.algorithmlearning.shared.auth.AuthFailure
import com.algorithmlearning.shared.auth.AuthFailureKind
import com.algorithmlearning.shared.auth.AuthRepository
import com.algorithmlearning.shared.auth.AuthSession
import com.algorithmlearning.shared.auth.AuthUser
import com.algorithmlearning.shared.library.ApiFailure
import com.algorithmlearning.shared.library.ApiFailureKind
import com.algorithmlearning.shared.library.Dashboard
import com.algorithmlearning.shared.library.DashboardRepository
import com.algorithmlearning.shared.library.Page
import com.algorithmlearning.shared.library.ProblemDetail
import com.algorithmlearning.shared.library.ProblemDifficulty
import com.algorithmlearning.shared.library.ProblemPlatform
import com.algorithmlearning.shared.library.ProblemQuery
import com.algorithmlearning.shared.library.ProblemRepository
import com.algorithmlearning.shared.library.ProblemSort
import com.algorithmlearning.shared.library.ProblemSummary
import com.algorithmlearning.shared.library.ProblemWrite
import com.algorithmlearning.shared.library.Review
import com.algorithmlearning.shared.library.ReviewRepository
import com.algorithmlearning.shared.library.ReviewStatus
import com.algorithmlearning.shared.library.ReviewSummary
import com.algorithmlearning.shared.library.Solution
import com.algorithmlearning.shared.library.SolutionLanguage
import com.algorithmlearning.shared.library.SolutionRepository
import com.algorithmlearning.shared.library.SolutionWrite
import com.algorithmlearning.shared.library.Tag
import com.algorithmlearning.shared.library.TagRepository
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * A stateful, test-only implementation of the frozen API at the shared
 * repository boundary. The acceptance suite drives the real presentation layer
 * and the real Compose screens through these repositories, so a problem created
 * in one experience is observable in the others without a network.
 *
 * It intentionally lives in `commonTest`, never production code, and reuses the
 * domain contracts that the app already depends on. Owner isolation,
 * authorization, and transport mapping remain covered by the API and data-layer
 * suites; this adapter verifies only client behavior.
 */
class ControlledApiAdapter {

    private val now: Instant = testInstant

    private val credentials = mutableMapOf<String, String>()
    private var session: AuthSession? = null

    private val storedProblems = LinkedHashMap<String, StoredProblem>()
    private val tagsById = LinkedHashMap<String, Tag>()
    private val solutionsByProblem = LinkedHashMap<String, MutableList<Solution>>()
    private val reviewsByProblem = LinkedHashMap<String, MutableList<Review>>()

    private var problemSequence = 0
    private var tagSequence = 0
    private var solutionSequence = 0
    private var reviewSequence = 0

    val auth: AuthRepository = AuthAdapter()
    val problems: ProblemRepository = ProblemAdapter()
    val tags: TagRepository = TagAdapter()
    val solutions: SolutionRepository = SolutionAdapter()
    val reviews: ReviewRepository = ReviewAdapter()
    val dashboard: DashboardRepository = DashboardAdapter()

    // --- Auth experience ----------------------------------------------------

    private inner class AuthAdapter : AuthRepository {
        override suspend fun login(email: String, password: String): AuthSession {
            if (credentials[email] != password) {
                throw AuthFailure(AuthFailureKind.INVALID_CREDENTIALS)
            }
            return startSession(email)
        }

        override suspend fun register(email: String, password: String): AuthSession {
            if (credentials.containsKey(email)) {
                throw AuthFailure(AuthFailureKind.INVALID_CREDENTIALS)
            }
            credentials[email] = password
            return startSession(email)
        }

        override suspend fun restore(): AuthSession? = session

        override suspend fun logout() {
            session = null
        }

        override suspend fun accessToken(): String? = session?.accessToken

        override suspend fun invalidateAccessToken() = Unit
    }

    private fun startSession(email: String): AuthSession {
        val created = AuthSession(
            user = AuthUser(id = "user-1", email = email),
            accessToken = "access-token",
            expiresAt = now + 1.days,
        )
        session = created
        return created
    }

    // --- Problem management experience --------------------------------------

    private inner class ProblemAdapter : ProblemRepository {
        override suspend fun list(query: ProblemQuery): Page<ProblemSummary> {
            val filtered = storedProblems.values.filter { stored -> stored.matches(query) }
            val sorted = filtered.sortedWith(comparatorFor(query.sort))
            val from = ((query.page - 1) * query.pageSize).coerceIn(0, sorted.size)
            val to = (from + query.pageSize).coerceAtMost(sorted.size)
            val items = sorted.subList(from, to).map { summaryOf(it) }
            val totalPages = if (sorted.isEmpty()) 1 else (sorted.size + query.pageSize - 1) / query.pageSize
            return Page(items, query.page, query.pageSize, sorted.size, totalPages)
        }

        override suspend fun get(id: String): ProblemDetail = detailOf(requireProblem(id))

        override suspend fun create(write: ProblemWrite): ProblemDetail {
            problemSequence++
            val stored = StoredProblem(
                id = "problem-$problemSequence",
                write = write,
                createdAt = now,
                updatedAt = now,
            )
            storedProblems[stored.id] = stored
            solutionsByProblem[stored.id] = mutableListOf()
            return detailOf(stored)
        }

        override suspend fun replace(id: String, write: ProblemWrite): ProblemDetail {
            val existing = requireProblem(id)
            val updated = existing.copy(write = write, updatedAt = now)
            storedProblems[id] = updated
            return detailOf(updated)
        }

        override suspend fun delete(id: String) {
            requireProblem(id)
            storedProblems.remove(id)
            solutionsByProblem.remove(id)
            reviewsByProblem.remove(id)
        }
    }

    // --- Tags ---------------------------------------------------------------

    private inner class TagAdapter : TagRepository {
        override suspend fun list(query: String?): List<Tag> =
            tagsById.values.filter { query.isNullOrBlank() || it.name.contains(query, ignoreCase = true) }

        override suspend fun create(name: String): Tag {
            val normalized = name.trim()
            val existing = tagsById.values.firstOrNull { it.name.equals(normalized, ignoreCase = true) }
            if (existing != null) return existing
            tagSequence++
            val created = Tag(id = "tag-$tagSequence", name = normalized)
            tagsById[created.id] = created
            return created
        }
    }

    // --- Solutions ----------------------------------------------------------

    private inner class SolutionAdapter : SolutionRepository {
        override suspend fun list(problemId: String): List<Solution> {
            requireProblem(problemId)
            return solutionsByProblem[problemId].orEmpty().toList()
        }

        override suspend fun create(problemId: String, write: SolutionWrite): Solution {
            requireProblem(problemId)
            solutionSequence++
            val created = Solution(
                id = "solution-$solutionSequence",
                problemId = problemId,
                language = write.language,
                code = write.code,
                explanation = write.explanation,
                createdAt = now,
                updatedAt = now,
            )
            solutionsByProblem.getOrPut(problemId) { mutableListOf() } += created
            return created
        }

        override suspend fun replace(id: String, write: SolutionWrite): Solution {
            val host = solutionsByProblem.entries.firstOrNull { entry -> entry.value.any { it.id == id } }
                ?: throw ApiFailure(ApiFailureKind.NOT_FOUND)
            val index = host.value.indexOfFirst { it.id == id }
            val existing = host.value[index]
            val updated = existing.copy(
                language = write.language,
                code = write.code,
                explanation = write.explanation,
                updatedAt = now,
            )
            host.value[index] = updated
            return updated
        }

        override suspend fun delete(id: String) {
            solutionsByProblem.values.forEach { list -> list.removeAll { it.id == id } }
        }
    }

    // --- Review experience --------------------------------------------------

    private inner class ReviewAdapter : ReviewRepository {
        override suspend fun submit(problemId: String, confidence: Int, notes: String?): Review {
            requireProblem(problemId)
            reviewSequence++
            val schedule = adaptiveSchedule(reviewsByProblem[problemId]?.lastOrNull(), confidence)
            val created = Review(
                id = "review-$reviewSequence",
                problemId = problemId,
                confidence = confidence,
                reviewedAt = now,
                nextReviewAt = now + schedule.intervalDays.days,
                notes = notes,
                policyVersion = "adaptive-v1",
                intervalDays = schedule.intervalDays,
                easeFactor = schedule.easeFactor,
                repetitions = schedule.repetitions,
                scheduleExplanationKey = "schedule.adaptive.rated",
            )
            reviewsByProblem.getOrPut(problemId) { mutableListOf() } += created
            return created
        }

        override suspend fun due(page: Int, pageSize: Int): List<ProblemSummary> =
            storedProblems.values
                .filter { summaryOf(it).review.status != ReviewStatus.SCHEDULED }
                .map { summaryOf(it) }

        override suspend fun history(problemId: String, page: Int, pageSize: Int): List<Review> {
            requireProblem(problemId)
            return reviewsByProblem[problemId].orEmpty().reversed()
        }
    }

    // --- Dashboard ----------------------------------------------------------

    private inner class DashboardAdapter : DashboardRepository {
        override suspend fun get(): Dashboard {
            val summaries = storedProblems.values.map { summaryOf(it) }
            return Dashboard(
                totalProblems = summaries.size.toLong(),
                easy = summaries.count { it.difficulty == ProblemDifficulty.EASY }.toLong(),
                medium = summaries.count { it.difficulty == ProblemDifficulty.MEDIUM }.toLong(),
                hard = summaries.count { it.difficulty == ProblemDifficulty.HARD }.toLong(),
                dueReviewCount = summaries.count { it.review.status != ReviewStatus.SCHEDULED }.toLong(),
            )
        }
    }

    // --- Test seeding and assertions ----------------------------------------

    /**
     * Synchronously seeds a problem, standing in for data another owner or a
     * prior session already persisted. Used to arrange a starting state before
     * a UI test that cannot suspend.
     */
    fun seedProblem(
        title: String,
        difficulty: ProblemDifficulty = ProblemDifficulty.EASY,
        platform: ProblemPlatform = ProblemPlatform.LEETCODE,
        notes: String? = null,
        keyInsight: String? = null,
    ): String {
        problemSequence++
        val id = "problem-$problemSequence"
        storedProblems[id] = StoredProblem(
            id = id,
            write = ProblemWrite(
                title = title,
                platform = platform,
                difficulty = difficulty,
                notes = notes,
                keyInsight = keyInsight,
            ),
            createdAt = now,
            updatedAt = now,
        )
        solutionsByProblem[id] = mutableListOf()
        return id
    }

    fun seedSolution(problemId: String, code: String): String {
        solutionSequence++
        val created = Solution(
            id = "solution-$solutionSequence",
            problemId = problemId,
            language = SolutionLanguage.KOTLIN,
            code = code,
            explanation = null,
            createdAt = now,
            updatedAt = now,
        )
        solutionsByProblem.getOrPut(problemId) { mutableListOf() } += created
        return created.id
    }

    /** The reviews recorded for a problem, newest first; safe to read in a UI test. */
    fun recordedReviews(problemId: String): List<Review> =
        reviewsByProblem[problemId].orEmpty().reversed()

    // --- Helpers ------------------------------------------------------------

    private fun requireProblem(id: String): StoredProblem =
        storedProblems[id] ?: throw ApiFailure(ApiFailureKind.NOT_FOUND)

    private fun summaryOf(stored: StoredProblem): ProblemSummary {
        val history = reviewsByProblem[stored.id].orEmpty()
        val last = history.lastOrNull()
        val status = when {
            last == null -> ReviewStatus.NEVER_REVIEWED
            last.nextReviewAt <= now -> ReviewStatus.DUE
            else -> ReviewStatus.SCHEDULED
        }
        return ProblemSummary(
            id = stored.id,
            title = stored.write.title,
            platform = stored.write.platform,
            externalProblemId = stored.write.externalProblemId,
            difficulty = stored.write.difficulty,
            tags = stored.write.tagIds.mapNotNull { tagsById[it] },
            review = ReviewSummary(
                status = status,
                confidence = last?.confidence,
                lastReviewedAt = last?.reviewedAt,
                nextReviewAt = last?.nextReviewAt,
                reviewCount = history.size,
            ),
            createdAt = stored.createdAt,
            updatedAt = stored.updatedAt,
        )
    }

    private fun detailOf(stored: StoredProblem): ProblemDetail = ProblemDetail(
        summary = summaryOf(stored),
        externalUrl = stored.write.externalUrl,
        description = stored.write.description,
        notes = stored.write.notes,
        keyInsight = stored.write.keyInsight,
        timeComplexity = stored.write.timeComplexity,
        spaceComplexity = stored.write.spaceComplexity,
        mistakes = stored.write.mistakes,
        interviewNotes = stored.write.interviewNotes,
        solutions = solutionsByProblem[stored.id].orEmpty().toList(),
    )

    private fun StoredProblem.matches(query: ProblemQuery): Boolean {
        val text = query.query
        if (!text.isNullOrBlank() && !write.title.contains(text, ignoreCase = true)) return false
        if (query.difficulty != null && write.difficulty != query.difficulty) return false
        if (query.platform != null && write.platform != query.platform) return false
        if (query.tagIds.isNotEmpty() && query.tagIds.none { it in write.tagIds }) return false
        if (query.reviewStatus != null && summaryOf(this).review.status != query.reviewStatus) return false
        return true
    }

    private fun comparatorFor(sort: ProblemSort): Comparator<StoredProblem> = when (sort) {
        ProblemSort.TITLE_ASC -> compareBy { it.write.title }
        ProblemSort.CREATED_DESC -> compareByDescending { it.createdAt }
        ProblemSort.UPDATED_DESC -> compareByDescending { it.updatedAt }
    }

    /**
     * The same deterministic adaptive-v1 progression the server applies, so the
     * acceptance journey can assert the schedule the client renders.
     */
    private fun adaptiveSchedule(previous: Review?, confidence: Int): Schedule {
        val bootstrap = previous?.intervalDays == null
        val priorInterval = previous?.intervalDays ?: 0
        val priorEase = previous?.easeFactor ?: 2.50
        val priorRepetitions = previous?.repetitions ?: 0
        val intervalDays: Int
        val repetitions: Int
        val delta: Double
        when (confidence) {
            0 -> { intervalDays = 1; repetitions = 0; delta = -0.20 }
            1 -> { intervalDays = 1; repetitions = 0; delta = -0.15 }
            2 -> {
                repetitions = maxOf(priorRepetitions, 1)
                intervalDays = maxOf(2, kotlin.math.round(priorInterval * 1.20).toInt())
                delta = -0.05
            }
            3 -> {
                repetitions = priorRepetitions + 1
                intervalDays = if (bootstrap) 4 else maxOf(4, kotlin.math.round(priorInterval * priorEase).toInt())
                delta = 0.0
            }
            else -> {
                repetitions = priorRepetitions + 1
                intervalDays = if (bootstrap) 7 else maxOf(7, kotlin.math.round(priorInterval * (priorEase + 0.15)).toInt())
                delta = 0.15
            }
        }
        return Schedule(intervalDays, (priorEase + delta).coerceIn(1.30, 3.00), repetitions)
    }

    private data class Schedule(val intervalDays: Int, val easeFactor: Double, val repetitions: Int)

    private data class StoredProblem(
        val id: String,
        val write: ProblemWrite,
        val createdAt: Instant,
        val updatedAt: Instant,
    )
}
