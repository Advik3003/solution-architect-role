package com.interview.platform.account.controller;

import com.interview.platform.account.service.AccountQueryService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts") // Version 1 endpoint contract
public class AccountControllerV1 {

    private final AccountQueryService accountQueryService;

    public AccountControllerV1(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAccount(@PathVariable String id) {
        // V1 response shape kept stable for existing consumers.
        return accountQueryService.getAccountV1(id);
    }
}
