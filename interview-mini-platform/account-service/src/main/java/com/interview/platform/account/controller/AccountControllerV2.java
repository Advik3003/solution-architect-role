package com.interview.platform.account.controller;

import com.interview.platform.account.service.AccountQueryService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/accounts") // Version 2 can evolve independently from v1
public class AccountControllerV2 {

    private final AccountQueryService accountQueryService;

    public AccountControllerV2(AccountQueryService accountQueryService) {
        this.accountQueryService = accountQueryService;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getAccount(@PathVariable String id) {
        // V2 adds richer fields while preserving old clients on v1.
        return accountQueryService.getAccountV2(id);
    }
}
