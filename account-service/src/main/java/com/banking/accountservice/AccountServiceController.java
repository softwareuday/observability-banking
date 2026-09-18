package com.banking.accountservice;

import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/accounts")
public class AccountServiceController {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceController.class);
    private final Tracer tracer;
    private final Random random = new Random();

    public AccountServiceController(Tracer tracer) {
        this.tracer = tracer;
    }

    @PostMapping("/debit")
    @Observed(name = "account.debit")
    public Map<String, Object> debit(@RequestBody Map<String, Object> req) {
        String account = String.valueOf(req.get("fromAccount"));
        Object amount = req.get("amount");

        log.info("Debiting account={} amount={}", account, amount);

        // Simulate 20% failure for demo (so you can see traces with errors)
        if (random.nextInt(10) < 2) {
            log.error("Debit failed — insufficient balance for account={}", account);
            throw new RuntimeException("INSUFFICIENT_BALANCE");
        }

        return Map.of("status", "DEBITED", "account", account, "balance", 5000);
    }

    @PostMapping("/credit")
    @Observed(name = "account.credit")
    public Map<String, Object> credit(@RequestBody Map<String, Object> req) {
        log.info("Crediting account={} amount={}", req.get("toAccount"), req.get("amount"));
        return Map.of("status", "CREDITED");
    }
}