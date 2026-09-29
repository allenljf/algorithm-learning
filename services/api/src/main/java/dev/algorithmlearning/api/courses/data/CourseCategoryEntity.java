package dev.algorithmlearning.api.courses.data;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="course_categories") class CourseCategoryEntity { @Id UUID id; String slug; @Column(name="display_name") String displayName; @Column(name="sort_order") int sortOrder; protected CourseCategoryEntity(){} CourseCategoryEntity(UUID id,String slug,String displayName,int sortOrder){this.id=id;this.slug=slug;this.displayName=displayName;this.sortOrder=sortOrder;} }
