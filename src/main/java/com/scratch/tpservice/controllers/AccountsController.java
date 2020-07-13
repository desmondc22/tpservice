package com.scratch.tpservice.controllers;

import com.scratch.tpservice.TpserviceApplication;
import com.scratch.tpservice.services.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @GetMapping(value="/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAccountBalances(@RequestParam MultiValueMap<String, String> accountMap) {

        List<String> accountIds = accountMap.get("accountId");
        List<Map<String, Object>> accounts = new ArrayList<Map<String,Object>>();

        if (accountIds != null && accountIds.size() > 0) {
            for (String accountId: accountIds) {
                // if accountID does not exist, getBalance in service will create one with default values.
                double balance = accountsService.getBalance(accountId);
                boolean frozen = accountsService.isFrozen(accountId);
                // account object for output
                Map<String, Object> account = new HashMap<String, Object>();
                account.put("accountId", accountId);
                account.put("balance", balance);
                account.put("frozen", frozen);
                // add account to return list
                accounts.add(account);
            }
        }
        return accounts;
    }
}
