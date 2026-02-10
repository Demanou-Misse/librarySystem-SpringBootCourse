package com.example.template.repository.specification;

import com.example.template.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class BookSpecification {

    // Filter by Title (Case insensitive + contains)
    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> title == null ? null :
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    // Filter by Author
    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> author == null ? null :
                cb.equal(root.get("author"), author);
    }

    // Filter by Price range
    public static Specification<Book> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }
}

