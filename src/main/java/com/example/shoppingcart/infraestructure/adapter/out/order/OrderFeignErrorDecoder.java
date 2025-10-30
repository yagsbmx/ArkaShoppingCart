package com.example.shoppingcart.infraestructure.adapter.out.order;

import feign.Response;
import feign.codec.ErrorDecoder;
import feign.FeignException;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class OrderFeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    try (Reader reader = response.body() != null ? response.body().asReader(StandardCharsets.UTF_8) : null) {
    
    } catch (Exception ignored) {}

    return FeignException.errorStatus(methodKey, response);
  }
}
