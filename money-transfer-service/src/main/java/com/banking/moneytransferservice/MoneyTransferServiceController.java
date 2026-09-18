package com.banking.moneytransferservice;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class MoneyTransferServiceController {

    private static final Logger log = LoggerFactory.getLogger(MoneyTransferServiceController.class);

    private final Tracer tracer;
    private final RestClient restClient;

    public MoneyTransferServiceController(Tracer tracer) {
        this.tracer = tracer;
        this.restClient = RestClient.create();
    }

    @PostMapping
    @Observed(name = "money.transfer", contextualName = "transfer-money")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody Map<String, Object> request) {
        String txnRef = "TXN-" + System.currentTimeMillis();

        // Create custom span for the core business step
        Span span = tracer.nextSpan().name("debit-account").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            span.tag("txn.ref", txnRef);
            span.tag("from.account", String.valueOf(request.get("fromAccount")));
            span.tag("to.account", String.valueOf(request.get("toAccount")));
            span.tag("amount", String.valueOf(request.get("amount")));

            log.info("Initiating transfer txnRef={} from={} to={} amount={}",
                txnRef,
                request.get("fromAccount"),
                request.get("toAccount"),
                request.get("amount"));

            // Call Account Service (distributed trace continues here)
            restClient.post()
                .uri("http://account-service:8082/api/accounts/debit")
                .body(request)
                .retrieve()
                .toBodilessEntity();

            span.event("debit-success");

        } catch (Exception e) {
            span.error(e);
            log.error("Transfer failed txnRef={} error={}", txnRef, e.getMessage(), e);
            throw e;
        } finally {
            span.end();
        }

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "txnRef", txnRef
        ));
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        log.debug("Health check called");
        return "OK";
    }
}