package com.example.shoppingcart.infraestructure.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FeignGenericErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado en servicio remoto");
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurso no encontrado en servicio remoto");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}

