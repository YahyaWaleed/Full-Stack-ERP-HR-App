package com.yahya.erphrapp.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// In-memory caches for reference data that only changes through migrations: leave types, tax brackets,
// payroll settings, salary components, branches, departments, job titles (see the @Cacheable methods).
// Restarting the app clears them.
@Configuration
@EnableCaching
public class CacheConfig {
}
