package com.algorithmlearning.shared.library

class FakeProblemRemote : ProblemRemote {
    var page: Page<ProblemSummary> = Page(listOf(testSummary()), 1, 20, 1, 1)
    var detail: ProblemDetail = testDetail()
    var lastQuery: ProblemQuery? = null
    var lastWrite: ProblemWrite? = null
    var lastReplacedId: String? = null
    var deletedId: String? = null

    override suspend fun list(query: ProblemQuery): Page<ProblemSummary> {
        lastQuery = query
        return page
    }

    override suspend fun get(id: String): ProblemDetail = detail

    override suspend fun create(write: ProblemWrite): ProblemDetail {
        lastWrite = write
        return detail
    }

    override suspend fun replace(id: String, write: ProblemWrite): ProblemDetail {
        lastReplacedId = id
        lastWrite = write
        return detail
    }

    override suspend fun delete(id: String) {
        deletedId = id
    }
}

class FakeTagRemote : TagRemote {
    var tags: List<Tag> = listOf(testTag())
    var created: Tag = testTag(id = "tag-2", name = "Graphs")
    var lastQuery: String? = null
    var lastCreateName: String? = null

    override suspend fun list(query: String?): List<Tag> {
        lastQuery = query
        return tags
    }

    override suspend fun create(name: String): Tag {
        lastCreateName = name
        return created
    }
}

class FakeSolutionRemote : SolutionRemote {
    var solutions: List<Solution> = listOf(testSolution())
    var lastProblemId: String? = null
    var lastWrite: SolutionWrite? = null
    var lastReplacedId: String? = null
    var deletedId: String? = null

    override suspend fun list(problemId: String): List<Solution> {
        lastProblemId = problemId
        return solutions
    }

    override suspend fun create(problemId: String, write: SolutionWrite): Solution {
        lastProblemId = problemId
        lastWrite = write
        return testSolution()
    }

    override suspend fun replace(id: String, write: SolutionWrite): Solution {
        lastReplacedId = id
        lastWrite = write
        return testSolution(id = id)
    }

    override suspend fun delete(id: String) {
        deletedId = id
    }
}

class FakeReviewRemote : ReviewRemote {
    var review: Review = testReview()
    var dueProblems: List<ProblemSummary> = listOf(testSummary())
    var history: List<Review> = listOf(testReview())
    var lastSubmit: Triple<String, Int, String?>? = null
    var lastDue: Pair<Int, Int>? = null
    var lastHistory: Triple<String, Int, Int>? = null

    override suspend fun submit(problemId: String, confidence: Int, notes: String?): Review {
        lastSubmit = Triple(problemId, confidence, notes)
        return review
    }

    override suspend fun due(page: Int, pageSize: Int): List<ProblemSummary> {
        lastDue = page to pageSize
        return dueProblems
    }

    override suspend fun history(problemId: String, page: Int, pageSize: Int): List<Review> {
        lastHistory = Triple(problemId, page, pageSize)
        return history
    }
}

class FakeDashboardRemote : DashboardRemote {
    var dashboard: Dashboard = testDashboard()
    var calls: Int = 0

    override suspend fun get(): Dashboard {
        calls++
        return dashboard
    }
}
