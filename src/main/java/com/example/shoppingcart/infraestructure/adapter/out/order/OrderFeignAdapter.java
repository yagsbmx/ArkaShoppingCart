package com.example.shoppingcart.infraestructure.adapter.out.order;

import com.example.shoppingcart.domain.ports.out.OrderPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFeignAdapter implements OrderPort {

    private final OrderFeignClient orderFeignClient;

    @Override
    public void sendOrder(OrderFeignRequestDto orderRequest) {
        try {
            OrderFeignResponseDto response = orderFeignClient.createOrder(orderRequest);

            if (response == null || response.getId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Order-Service respondió vacío o sin ID"
                );
            }

            log.info("Orden creada en Order-Service. id={}, status={}", response.getId(), response.getStatus());

        } catch (FeignException fe) {
            String body;
            try {
                body = fe.contentUTF8();
            } catch (Throwable t) {
                body = fe.getMessage();
            }

            log.error("Error al llamar Order-Service: status={}, body={}", fe.status(), body);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "order-service respondió " + fe.status() + ": " + body,
                    fe
            );

        } catch (Exception ex) {
            log.error("Error inesperado al crear la orden: {}", ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error inesperado al crear la orden: " + ex.getMessage(),
                    ex
            );
        }
    }
}

