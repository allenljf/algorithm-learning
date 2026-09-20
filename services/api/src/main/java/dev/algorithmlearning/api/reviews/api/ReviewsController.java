package dev.algorithmlearning.api.reviews.api;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.problems.api.ProblemsController;
import dev.algorithmlearning.api.problems.application.ProblemService;
import dev.algorithmlearning.api.reviews.application.Review;
import dev.algorithmlearning.api.reviews.application.ReviewService;
import dev.algorithmlearning.api.reviews.domain.AdaptiveReviewPolicy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsController {

    private final ReviewService reviews;
    private final ProblemService problems;
    private final CurrentUser user;

    public ReviewsController(ReviewService reviews, ProblemService problems, CurrentUser user) {
        this.reviews = reviews;
        this.problems = problems;
        this.user = user;
    }

    @PostMapping
    public ResponseEntity<View> create(@Valid @RequestBody Write write) {
        return reviews.create(user.id(), write.problemId(), write.confidence(), write.notes())
                .map(review -> ResponseEntity.status(201).body(View.from(review)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/today")
    public List<ProblemsController.Summary> due(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        var ids = reviews.dueProblemIds(user.id(), page, pageSize);
        if (ids.isEmpty()) return List.of();
        var problemsById = problems.findByIds(user.id(), ids);
        var reviewSummaries = reviews.summaries(user.id(), ids);
        return ids.stream()
                .filter(problemsById::containsKey)
                .map(id -> ProblemsController.Summary.from(problemsById.get(id), reviewSummaries.get(id)))
                .toList();
    }

    @GetMapping("/history")
    public List<View> history(
            @RequestParam(required = false) UUID problemId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        if (problemId == null) return List.of();
        return reviews.history(user.id(), problemId, page, pageSize).stream().map(View::from).toList();
    }

    record Write(@NotNull UUID problemId, @Min(0) @Max(4) int confidence, @Size(max = 10000) String notes) { }

    record View(UUID id, UUID problemId, int confidence, Instant reviewedAt, Instant nextReviewAt, String notes,
                String policyVersion, Integer intervalDays, BigDecimal easeFactor, Integer repetitions,
                String scheduleExplanationKey) {
        static View from(Review review) {
            var key = AdaptiveReviewPolicy.VERSION.equals(review.policyVersion())
                    ? "schedule.adaptive.rated"
                    : "schedule.fixed.rated";
            return new View(review.id(), review.problemId(), review.confidence(), review.reviewedAt(),
                    review.nextReviewAt(), review.notes(), review.policyVersion(), review.intervalDays(),
                    review.easeFactor(), review.repetitions(), key);
        }
    }
}
