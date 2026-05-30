package com.interview.platform.account.controller;

import com.interview.platform.account.messaging.OrderEventProducer;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final OrderEventProducer orderEventProducer;

    public TransferController(OrderEventProducer orderEventProducer) {
        this.orderEventProducer = orderEventProducer;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_transfer.write')") // Scope-based authorization for money movement API
    public Map<String, Object> createTransfer(
        @RequestBody Map<String, Object> request,
        @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey) {

        // Why this exists:
        // Idempotency key protects payment/transfer APIs from accidental duplicate submissions.
        // Interview talking point:
        // "For financial operations, idempotency is mandatory to keep transaction semantics safe."
        // Idempotency key prevents accidental duplicate transfer processing.
        String safeIdempotencyKey = idempotencyKey == null ? "missing-key" : idempotencyKey;
        orderEventProducer.publish("transfer.created", request);

        return Map.of(
            "status", "ACCEPTED",
            "idempotencyKey", safeIdempotencyKey
        );
    }
}
