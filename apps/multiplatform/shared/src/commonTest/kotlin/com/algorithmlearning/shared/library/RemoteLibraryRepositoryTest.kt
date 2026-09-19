package com.algorithmlearning.shared.library

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteLibraryRepositoryTest {

    @Test
    fun problemRepositoryDelegatesEveryCall() = runTest {
        val remote = FakeProblemRemote()
        val repository = RemoteProblemRepository(remote)
        val query = ProblemQuery(query = "sum")

        assertEquals(remote.page, repository.list(query))
        assertEquals(query, remote.lastQuery)
        assertEquals(remote.detail, repository.get("problem-1"))
        assertEquals(remote.detail, repository.create(ProblemWrite("T", ProblemPlatform.OTHER, ProblemDifficulty.HARD)))
        assertEquals(remote.detail, repository.replace("problem-1", ProblemWrite("T", ProblemPlatform.OTHER, ProblemDifficulty.HARD)))
        assertEquals("problem-1", remote.lastReplacedId)
        repository.delete("problem-1")
        assertEquals("problem-1", remote.deletedId)
    }

    @Test
    fun tagRepositoryDelegatesEveryCall() = runTest {
        val remote = FakeTagRemote()
        val repository = RemoteTagRepository(remote)

        assertEquals(remote.tags, repository.list("arr"))
        assertEquals("arr", remote.lastQuery)
        assertEquals(remote.created, repository.create("Graphs"))
        assertEquals("Graphs", remote.lastCreateName)
    }

    @Test
    fun solutionRepositoryDelegatesEveryCall() = runTest {
        val remote = FakeSolutionRemote()
        val repository = RemoteSolutionRepository(remote)
        val write = SolutionWrite(SolutionLanguage.KOTLIN, "code")

        assertEquals(remote.solutions, repository.list("problem-1"))
        assertEquals(remote.solutions.single(), repository.create("problem-1", write))
        assertEquals("problem-1", remote.lastProblemId)
        repository.replace("solution-1", write)
        assertEquals("solution-1", remote.lastReplacedId)
        repository.delete("solution-1")
        assertEquals("solution-1", remote.deletedId)
    }

    @Test
    fun reviewRepositoryDelegatesEveryCall() = runTest {
        val remote = FakeReviewRemote()
        val repository = RemoteReviewRepository(remote)

        assertEquals(remote.review, repository.submit("problem-1", 3, "nice"))
        assertEquals(Triple("problem-1", 3, "nice"), remote.lastSubmit)
        assertEquals(remote.dueProblems, repository.due(2, 5))
        assertEquals(2 to 5, remote.lastDue)
        assertEquals(remote.history, repository.history("problem-1", 3, 7))
        assertEquals(Triple("problem-1", 3, 7), remote.lastHistory)
    }

    @Test
    fun dashboardRepositoryDelegatesAndReturnsCounts() = runTest {
        val remote = FakeDashboardRemote()
        val repository = RemoteDashboardRepository(remote)

        assertEquals(remote.dashboard, repository.get())
        assertEquals(1, remote.calls)
    }
}
