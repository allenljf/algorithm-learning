package com.algorithmlearning.shared.library

internal const val tagJson = """{"id":"tag-1","name":"Arrays"}"""

internal const val solutionJson =
    """{"id":"solution-1","problemId":"problem-1","language":"kotlin","code":"fun twoSum() = Unit","explanation":"Map complements.","createdAt":"2026-01-01T00:00:00Z","updatedAt":"2026-01-01T00:00:00Z"}"""

internal const val reviewJson =
    """{"id":"review-1","problemId":"problem-1","confidence":3,"reviewedAt":"2026-01-01T00:00:00Z","nextReviewAt":"2026-01-08T00:00:00Z","notes":"Solid.","policyVersion":"mvp-1"}"""

internal const val summaryJson =
    """{"id":"problem-1","title":"Two Sum","platform":"hacker_rank","externalProblemId":"1","difficulty":"easy","tags":[$tagJson],"review":{"status":"neverReviewed","confidence":null,"lastReviewedAt":null,"nextReviewAt":"2026-01-01T00:00:00Z","reviewCount":0},"createdAt":"2026-01-01T00:00:00Z","updatedAt":"2026-01-02T00:00:00Z"}"""

internal const val detailJson =
    """{"id":"problem-1","title":"Two Sum","platform":"leetcode","externalProblemId":"1","externalUrl":"https://leetcode.com/problems/two-sum","difficulty":"easy","tags":[$tagJson],"review":{"status":"neverReviewed","confidence":null,"lastReviewedAt":null,"nextReviewAt":"2026-01-01T00:00:00Z","reviewCount":0},"description":"Find two numbers.","notes":"Use a hash map.","keyInsight":"Complement lookup.","timeComplexity":"O(n)","spaceComplexity":"O(n)","mistakes":"Forgetting duplicates.","interviewNotes":"Clarify constraints.","solutions":[$solutionJson],"createdAt":"2026-01-01T00:00:00Z","updatedAt":"2026-01-02T00:00:00Z"}"""

internal const val dashboardJson =
    """{"totalProblems":4,"easy":2,"medium":1,"hard":1,"dueReviewCount":1}"""
