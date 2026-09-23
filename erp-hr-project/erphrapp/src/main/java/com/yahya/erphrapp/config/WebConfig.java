package com.yahya.erphrapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.DIRECT;

// Paged responses keep their current JSON shape ({content, totalPages, totalElements, number, size, ...}),
// which the frontend and API clients rely on. Declaring it explicitly pins that contract (and silences
// Spring Data's "no stability guarantee" warning).
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = DIRECT)
public class WebConfig {
}
