package com.growfund.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor());
    }

    private static class RateLimitInterceptor implements HandlerInterceptor {
        // Simple in-memory rate limiter: 100 req/min per IP
        private final Map<String, RequestCounter> viewCounts = new ConcurrentHashMap<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            String clientIp = request.getRemoteAddr();
            RequestCounter counter = viewCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

            if (counter.isBlocked()) {
                response.setStatus(429); // Too Many Requests
                return false;
            }

            counter.increment();
            return true;
        }

        private static class RequestCounter {
            private final AtomicInteger count = new AtomicInteger(0);
            private long startTime = System.currentTimeMillis();

            synchronized void increment() {
                long now = System.currentTimeMillis();
                if (now - startTime > 60000) { // 1 minute window
                    count.set(0);
                    startTime = now;
                }
                count.incrementAndGet();
            }

            synchronized boolean isBlocked() {
                long now = System.currentTimeMillis();
                if (now - startTime > 60000) {
                    count.set(0);
                    startTime = now;
                }
                return count.get() > 100; // Limit: 100 requests per minute
            }
        }
    }
}
