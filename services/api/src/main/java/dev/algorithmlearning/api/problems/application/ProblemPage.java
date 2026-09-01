package dev.algorithmlearning.api.problems.application;

import java.util.List;

public record ProblemPage(List<Problem> items, long totalItems) { }
