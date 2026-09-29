package dev.algorithmlearning.api.courses.application;
import java.time.Clock; import java.time.Instant; import java.util.*;
public class CourseService {
 private final CourseRepository repository; private final Clock clock;
 public CourseService(CourseRepository repository, Clock clock){this.repository=repository;this.clock=clock;}
 public List<CourseLesson> upsert(List<CourseImport> imports){return imports.stream().map(this::upsert).toList();}
 private CourseLesson upsert(CourseImport in){
  if(in.tags()==null || in.tags().isEmpty() || in.detail()==null || in.detail().isBlank()) throw new IllegalArgumentException("invalid course lesson");
  var category=repository.findCategoryBySlug(in.categorySlug()).orElseGet(()->repository.saveCategory(new CourseCategory(UUID.randomUUID(),in.categorySlug(),in.categoryDisplayName(),in.categorySortOrder())));
  var now=Instant.now(clock); var prior=repository.findLessonBySourceIdentity(in.sourceIdentity());
  var lesson=new CourseLesson(prior.map(CourseLesson::id).orElseGet(UUID::randomUUID),category,in.sourceIdentity(),in.sourceMarkdownPath(),in.englishTitle(),List.copyOf(in.tags()),in.detail(),in.sourceUrl(),in.sourceType(),in.sortOrder(),prior.map(CourseLesson::createdAt).orElse(now),now);
  return repository.saveLesson(lesson);
 }
 public CoursePage list(String category,String tag,String keyword,int page,int pageSize){ if(page<1||pageSize<1||pageSize>100)throw new IllegalArgumentException("invalid page"); var all=repository.lessons(category,tag,keyword); int from=Math.min((page-1)*pageSize,all.size()),to=Math.min(from+pageSize,all.size()); return new CoursePage(all.subList(from,to),page,pageSize,all.size()); }
 public Optional<CourseLesson> detail(String sourceIdentity){return repository.findLessonByIdentity(sourceIdentity);}
 public List<CourseCategory> categories(){return repository.categories();}
}
