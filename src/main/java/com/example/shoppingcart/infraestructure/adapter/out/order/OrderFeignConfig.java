package com.example.shoppingcart.infraestructure.adapter.out.order;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Configuration
public class OrderFeignConfig {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new OrderFeignErrorDecoder(); 
  }

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public RequestInterceptor authAndCorrelationForwarder() {
    return template -> {
      var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attrs != null) {
        var req = attrs.getRequest();
        var auth = req.getHeader("Authorization");
        if (auth != null) template.header("Authorization", auth);

        var cid = Optional.ofNullable(req.getHeader("X-Correlation-Id"))
                .orElse(UUID.randomUUID().toString());
        template.header("X-Correlation-Id", cid);
      }
      template.header("Content-Type", "application/json");
      template.header("Accept", "application/json");
    };
  }
}
