package com.example.shoppingcart.infraestructure.adapter.out.product;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProductFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado en product-service");
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado en product-service");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
