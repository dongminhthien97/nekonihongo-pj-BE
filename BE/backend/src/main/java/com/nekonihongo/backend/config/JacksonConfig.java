// src/main/java/com/nekonihongo/backend/config/JacksonConfig.java (tạo file mới để fix lỗi ObjectMapper bean)

package com.nekonihongo.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary // Đảm bảo bean này được ưu tiên nếu có nhiều ObjectMapper
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Đăng ký module cho LocalDateTime, LocalDate (JavaTimeModule)
        mapper.registerModule(new JavaTimeModule());

        // Tắt viết date dưới dạng timestamp (để serialize LocalDateTime thành string
        // ISO)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Các config khác nếu cần (ví dụ: INDENT_OUTPUT cho pretty print debug)
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }
}