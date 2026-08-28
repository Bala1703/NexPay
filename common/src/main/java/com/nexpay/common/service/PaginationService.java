package com.nexpay.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;

import java.util.List;


public class PaginationService {

    public <T> Page<T> paginate(
            List<T> items,
            Pageable pageable
    ) {

        int start = (int) pageable.getOffset();

        int end = Math.min(
                start + pageable.getPageSize(),
                items.size()
        );

        if (start >= items.size()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    items.size()
            );
        }

        List<T> pageContent = items.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                items.size()
        );
    }
}