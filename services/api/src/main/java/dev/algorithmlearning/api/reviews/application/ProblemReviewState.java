package dev.algorithmlearning.api.reviews.application;

/** A problem's latest review event paired with its total event count. */
public record ProblemReviewState(Review latest, int reviewCount) { }
