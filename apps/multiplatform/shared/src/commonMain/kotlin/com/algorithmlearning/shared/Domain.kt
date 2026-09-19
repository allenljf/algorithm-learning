package com.algorithmlearning.shared

/**
 * Foundation placeholder for the problem library. Later data tasks
 * (CMP-002/CMP-003) expand this beside the frozen REST contract; it exists now
 * so the smoke test can assert an immutable domain value.
 */
data class Problem(
    val id: String,
    val title: String,
    val difficulty: Difficulty,
)

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
}
