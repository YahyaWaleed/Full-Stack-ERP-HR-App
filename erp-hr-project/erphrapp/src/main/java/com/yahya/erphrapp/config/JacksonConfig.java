package com.yahya.erphrapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // without JavaTimeModule, Spring Boot will either crash or serialize a LocalDate as a weird array like [2026,7,1] instead of "2026-07-01".
    // This one bean fixes that for your whole app — every DTO with a date field benefits automatically, you don't configure anything per-DTO.

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
