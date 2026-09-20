package dev.algorithmlearning.api.problems.api;

import dev.algorithmlearning.api.app.security.CurrentUser;
import dev.algorithmlearning.api.problems.application.Problem;
import dev.algorithmlearning.api.problems.application.ProblemSearch;
import dev.algorithmlearning.api.problems.application.ProblemService;
import dev.algorithmlearning.api.problems.application.ProblemWrite;
import dev.algorithmlearning.api.problems.application.Solution;
import dev.algorithmlearning.api.problems.application.SolutionService;
import dev.algorithmlearning.api.reviews.application.ReviewService;
import dev.algorithmlearning.api.reviews.application.ReviewSummaryData;
import dev.algorithmlearning.api.tags.application.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ProblemsController.PATH)
public class ProblemsController {

    public static final String PATH = "/api/v1/problems";

    private final ProblemService problems;
    private final SolutionService solutions;
    private final ReviewService reviews;
    private final CurrentUser user;

    public ProblemsController(ProblemService problems, SolutionService solutions, ReviewService reviews, CurrentUser user) {
        this.problems = problems;
        this.solutions = solutions;
        this.reviews = reviews;
        this.user = user;
    }

    @GetMapping
    public PageResponse<Summary> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false, name = "tagId") List<UUID> tagIds,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(defaultValue = "updatedDesc") String sort,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize) {
        var result = problems.search(user.id(), new ProblemSearch(q, difficulty, platform, tagIds == null ? List.of() : tagIds, reviewStatus, sort, page, pageSize));
        var ids = result.items().stream().map(Problem::id).toList();
        var reviewSummaries = reviews.summaries(user.id(), ids);
        var items = result.items().stream().map(problem -> Summary.from(problem, reviewSummaries.get(problem.id()))).toList();
        var totalPages = (int) ((result.totalItems() + pageSize - 1) / pageSize);
        return new PageResponse<>(items, page, pageSize, (int) result.totalItems(), totalPages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Detail> get(@PathVariable UUID id) {
        return problems.findById(user.id(), id)
                .map(problem -> ResponseEntity.ok(Detail.from(problem, solutions.list(user.id(), problem.id()), review(user.id(), problem))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Detail> create(@Valid @RequestBody Write request) {
        var problem = problems.create(user.id(), request.toWrite());
        return ResponseEntity.created(URI.create(PATH + "/" + problem.id()))
                .body(Detail.from(problem, List.of(), review(user.id(), problem)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Detail> replace(@PathVariable UUID id, @Valid @RequestBody Write request) {
        return problems.replace(user.id(), id, request.toWrite())
                .map(problem -> ResponseEntity.ok(Detail.from(problem, solutions.list(user.id(), problem.id()), review(user.id(), problem))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return problems.delete(user.id(), id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ReviewSummaryData review(UUID owner, Problem problem) {
        return reviews.summaries(owner, List.of(problem.id())).get(problem.id());
    }

    public record Write(
            @NotBlank @Size(max = 200) String title,
            @NotBlank String platform,
            @Size(max = 120) String externalProblemId,
            @Size(max = 2000) String externalUrl,
            @NotBlank String difficulty,
            @Size(max = 20000) String description,
            @Size(max = 20000) String notes,
            @Size(max = 10000) String keyInsight,
            @Size(max = 200) String timeComplexity,
            @Size(max = 200) String spaceComplexity,
            @Size(max = 20000) String mistakes,
            @Size(max = 20000) String interviewNotes,
            @Size(max = 50) List<UUID> tagIds) {
        ProblemWrite toWrite() {
            return new ProblemWrite(title, platform, externalProblemId, externalUrl, difficulty, description, notes, keyInsight, timeComplexity, spaceComplexity, mistakes, interviewNotes, tagIds);
        }
    }

    public record TagSummary(UUID id, String name) {
        static TagSummary from(Tag tag) {
            return new TagSummary(tag.id(), tag.name());
        }
    }

    public record PageResponse<T>(List<T> items, int page, int pageSize, int totalItems, int totalPages) { }

    public record ReviewSummary(String status, Integer confidence, Instant lastReviewedAt, Instant nextReviewAt,
                                int reviewCount, Integer intervalDays, String scheduleExplanationKey) {
        public static ReviewSummary from(ReviewSummaryData data) {
            return new ReviewSummary(data.status(), data.confidence(), data.lastReviewedAt(), data.nextReviewAt(),
                    data.reviewCount(), data.intervalDays(), data.scheduleExplanationKey());
        }

        static ReviewSummary of(Problem problem, ReviewSummaryData data) {
            return data == null ? neverReviewed(problem) : from(data);
        }

        static ReviewSummary neverReviewed(Problem problem) {
            return new ReviewSummary("neverReviewed", null, null, problem.createdAt(), 0, null, "schedule.neverReviewed");
        }
    }

    public record Summary(UUID id, String title, String platform, String externalProblemId, String difficulty,
                          List<TagSummary> tags, ReviewSummary review, Instant createdAt, Instant updatedAt) {
        public static Summary from(Problem problem, ReviewSummaryData review) {
            return new Summary(problem.id(), problem.title(), problem.platform(), problem.externalProblemId(),
                    problem.difficulty(), problem.tags().stream().map(TagSummary::from).toList(),
                    ReviewSummary.of(problem, review), problem.createdAt(), problem.updatedAt());
        }
    }

    public record Detail(UUID id, String title, String platform, String externalProblemId, String externalUrl,
                         String difficulty, List<TagSummary> tags, ReviewSummary review, String description,
                         String notes, String keyInsight, String timeComplexity, String spaceComplexity,
                         String mistakes, String interviewNotes, List<SolutionsController.View> solutions,
                         Instant createdAt, Instant updatedAt) {
        static Detail from(Problem problem, List<Solution> solutions, ReviewSummaryData review) {
            return new Detail(problem.id(), problem.title(), problem.platform(), problem.externalProblemId(),
                    problem.externalUrl(), problem.difficulty(), problem.tags().stream().map(TagSummary::from).toList(),
                    ReviewSummary.of(problem, review), problem.description(), problem.notes(), problem.keyInsight(),
                    problem.timeComplexity(), problem.spaceComplexity(), problem.mistakes(), problem.interviewNotes(),
                    solutions.stream().map(SolutionsController.View::from).toList(), problem.createdAt(), problem.updatedAt());
        }
    }
}
