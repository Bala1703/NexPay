package com.nexpay.common.config;

import com.nexpay.common.service.PaginationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public PaginationService paginationService() {
        return new PaginationService();
    }
}