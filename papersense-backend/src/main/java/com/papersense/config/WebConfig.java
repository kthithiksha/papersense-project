package com.papersense.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Allows the React (Vite) frontend running on a different port
 * to call this backend's REST APIs during development.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${CORS_ALLOWED_ORIGINS:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if ("*".equals(allowedOrigins.trim())) {
            registry.addMapping("/api/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
        } else {
            registry.addMapping("/api/**")
                    .allowedOrigins(Arrays.stream(allowedOrigins.split(","))
                            .map(String::trim)
                            .filter(origin -> !origin.isEmpty())
                            .toArray(String[]::new))
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
        }
    }
}
