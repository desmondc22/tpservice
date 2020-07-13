package com.scratch.tpservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratch.tpservice.models.*;
import com.scratch.tpservice.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class TransactionsController {

    @Autowired
    TransactionsService transactionService;

    @PostMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> createTransactions(@RequestBody List<Map<String, Object>> transactions) {

        // find all the ill-format transactions
        // -- remove them and put them in invalidTransactions
        // the rest of the valid transactions (which are formatted correctly)
        // for all the invalid transactions from service ->
        // -- convert them back to raw object for JSON
        // -- add it to the list of failedTransactions

        List<Map<String, Object>> invalidJsonTransactions = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> validJsonTransactions = new ArrayList<Map<String, Object>>();

        for(Map<String, Object> transaction: transactions){
            if (isValid(transaction)) {
                validJsonTransactions.add(transaction);
            } else {
                invalidJsonTransactions.add(transaction);
            }
        }

        List<Transaction> transactionsList = toObjTransactions(validJsonTransactions);
        List<Transaction> failedTransactions = transactionService.executeTransactions(transactionsList);

        if (failedTransactions != null && failedTransactions.size() > 0) {
            invalidJsonTransactions.addAll(toJsonTransactions(failedTransactions));
        }

        return invalidJsonTransactions;
    }

    private List<Transaction> toObjTransactions(List<Map<String, Object>> transactions){
        List<Transaction> transactionsList = new ArrayList<>();
        for(Map<String, Object> trans : transactions){
            String transactionType = (String) trans.get("cmd");
            Transaction transaction = null;
            switch(transactionType) {
                case "DEPOSIT":
                    transaction = new Deposit(transactionType);
                    ((Deposit)transaction).setAmount((Double) trans.get("amount"));
                    ((Deposit)transaction).setAccountId((String) trans.get("accountId"));
                    break;
                case "WITHDRAW":
                    transaction = new Withdraw(transactionType);
                    ((Withdraw)transaction).setAmount((Double) trans.get("amount"));
                    ((Withdraw)transaction).setAccountId((String) trans.get("accountId"));
                    break;
                case "FREEZE":
                    transaction = new Freeze(transactionType);
                    ((Freeze)transaction).setAccountId((String) trans.get("accountId"));
                    break;
                case "THAW":
                    transaction = new Thaw(transactionType);
                    ((Thaw)transaction).setAccountId((String) trans.get("accountId"));
                    break;
                case "XFER":
                    transaction = new Xfer(transactionType);
                    ((Xfer)transaction).setAmount((Double) trans.get("amount"));
                    ((Xfer)transaction).setToId((String) trans.get("toId"));
                    ((Xfer)transaction).setFromId((String) trans.get("fromId"));
                    break;
                default:
                    break;
            }
            transactionsList.add(transaction);
//        });
        }
        return transactionsList;
    }

    private List<Map<String, Object>> toJsonTransactions(List<Transaction>  transactions){
        ObjectMapper objMapper = new ObjectMapper();
        List<Map<String, Object>> jsonTransactions = new ArrayList<Map<String,Object>>();

        for(Transaction transaction : transactions){
            Map<String, Object> transactionObj = objMapper.convertValue(transaction, Map.class);
            jsonTransactions.add(transactionObj);
        }
        return jsonTransactions;
    }

    private boolean isValid(Map<String, Object> transaction) {
        if(!transaction.containsKey("cmd")){
            return false;
        }
        String transactionType = (String) transaction.get("cmd");
        switch(transactionType) {
            case "DEPOSIT":
            case "WITHDRAW":
                return transaction.get("accountId") != null && transaction.get("amount") != null;
            case "FREEZE":
            case "THAW":
                return transaction.get("accountId") != null;
            case "XFER":
                return transaction.get("fromId") != null && transaction.get("toId") != null
                        && transaction.get("amount") != null;
        }
        return false;
    }
}