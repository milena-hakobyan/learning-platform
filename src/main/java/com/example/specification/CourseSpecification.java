package com.example.specification;

import com.example.model.Course;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;


public class CourseSpecification implements Specification<Course> {
    private SearchCriteria criteria;

    public CourseSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Path<?> path = root.get(criteria.getKey());
        Object value = criteria.getValue();

        if (path.getJavaType().equals(LocalDateTime.class) && value instanceof String) {
            value = LocalDateTime.parse((String) value);
        }

        switch (criteria.getOperation()) {
            case ">":
                if (value instanceof Comparable) {
                    return builder.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
                }
                break;
            case "<":
                if (value instanceof Comparable) {
                    return builder.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
                }
                break;
            case ":":
                if (path.getJavaType().equals(String.class)) {
                    return builder.like(path.as(String.class), "%" + value + "%");
                } else {
                    return builder.equal(path, value);
                }
            case "in":
                if (value instanceof List<?> list) {
                    CriteriaBuilder.In<Object> inClause = builder.in(path);
                    for (Object v : list) inClause.value(v);
                    return inClause;
                }
                break;
        }

        return null;
    }


}
