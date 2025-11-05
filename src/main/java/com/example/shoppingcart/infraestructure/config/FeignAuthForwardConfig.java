package com.example.shoppingcart.infraestructure.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration(proxyBeanMethods = false)
public class FeignAuthForwardConfig {

    @Bean
    public RequestInterceptor authForwardingInterceptor() {
        return template -> {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                var request = servletAttrs.getRequest();
                var authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isBlank()) {
                    template.header("Authorization", authHeader);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignGenericErrorDecoder();
    }
}

