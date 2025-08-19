package com.example.tasks;

import com.example.model.Course;
import com.example.specification.CourseSpecification;
import com.example.specification.SearchCriteria;
import org.junit.jupiter.api.Test;

import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CourseSpecificationTest {

    @Test
    void testGreaterThanOrEqualTo_withComparable() {
        SearchCriteria criteria = new SearchCriteria("createdAt", ">", LocalDateTime.now().minusDays(1).toString());
        CourseSpecification spec = new CourseSpecification(criteria);

        Root<Course> root = mock(Root.class);
        Path path = mock(Path.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        CriteriaQuery query = mock(CriteriaQuery.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("createdAt")).thenReturn(path);
        when(path.getJavaType()).thenReturn(LocalDateTime.class);
        when(builder.greaterThanOrEqualTo(any(Path.class), any(LocalDateTime.class))).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, builder);

        assertThat(result).isSameAs(predicate);
        verify(builder).greaterThanOrEqualTo(any(Path.class), any(LocalDateTime.class));
    }

    @Test
    void testLessThanOrEqualTo_withComparable() {
        SearchCriteria criteria = new SearchCriteria("createdAt", "<", LocalDateTime.now().toString());
        CourseSpecification spec = new CourseSpecification(criteria);

        Root<Course> root = mock(Root.class);
        Path path = mock(Path.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        CriteriaQuery query = mock(CriteriaQuery.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("createdAt")).thenReturn(path);
        when(path.getJavaType()).thenReturn(LocalDateTime.class);
        when(builder.lessThanOrEqualTo(any(Path.class), any(LocalDateTime.class))).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, builder);

        assertThat(result).isSameAs(predicate);
        verify(builder).lessThanOrEqualTo(any(Path.class), any(LocalDateTime.class));
    }

    @Test
    void testExactStringMatch() {
        SearchCriteria criteria = new SearchCriteria("title", ":", "Spring Boot");
        CourseSpecification spec = new CourseSpecification(criteria);

        Root<Course> root = mock(Root.class);
        Path path = mock(Path.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        CriteriaQuery query = mock(CriteriaQuery.class);
        Predicate predicate = mock(Predicate.class);

        when(root.get("title")).thenReturn(path);
        when(path.getJavaType()).thenReturn(String.class);
        when(builder.like(path.as(String.class), "%Spring Boot%")).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, builder);

        assertThat(result).isSameAs(predicate);
        verify(builder).like(path.as(String.class), "%Spring Boot%");
    }

}
