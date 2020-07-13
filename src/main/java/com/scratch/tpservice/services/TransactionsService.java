package com.scratch.tpservice.services;

import com.scratch.tpservice.ErrorCode;
import com.scratch.tpservice.TspException;
import com.scratch.tpservice.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionsService {

    @Autowired
    private AccountsService accountsService;


    public List<Transaction> executeTransactions(List<Transaction> transactions) {

        // keep a list of all the failed transaction  - returned in the response
        List<Transaction> failedTransactions = new ArrayList<Transaction>();

        for(Transaction transaction : transactions){
            try {
                switch (transaction.getCmd()) {
                    case "DEPOSIT":
                        executeDeposit((Deposit) transaction);
                        break;
                    case "WITHDRAW":
                        executeWithdraw((Withdraw) transaction);
                        break;
                    case "FREEZE":
                        executeFreeze((Freeze) transaction);
                        break;
                    case "THAW":
                        executeThaw((Thaw) transaction);
                        break;
                    case "XFER":
                        executeXfer((Xfer) transaction);
                        break;
                    default:
                        break;
                }
            }
            catch(TspException ex) {
                failedTransactions.add(transaction);
            }
        }

        return failedTransactions;
    }

    private void executeXfer(Xfer transaction) throws TspException {

        String toAccountId = transaction.getToId();
        double amount = transaction.getAmount();
        String fromAccountId = transaction.getFromId();
        //
        accountsService.withdraw(fromAccountId, amount);
        //
        try {
            accountsService.deposit(toAccountId, amount);
        } catch (TspException e) {
            //
            accountsService.deposit(fromAccountId, amount);
            throw new TspException(ErrorCode.FROZEN_ACCOUNT);
        }
    }

    private void executeThaw(Thaw transaction) {
        String accountId = transaction.getAccountId();
        accountsService.thawAccount(accountId);
    }

    private void executeFreeze(Freeze transaction) {
        String accountId = transaction.getAccountId();
        accountsService.freezeAccount(accountId);
    }

    private void executeWithdraw(Withdraw transaction) throws TspException {
        String accountId = transaction.getAccountId();
        double amount = transaction.getAmount();
        accountsService.withdraw(accountId, amount);
    }

    private void executeDeposit(Deposit transaction) throws TspException {
        String accountId = transaction.getAccountId();
        double amount = transaction.getAmount();
        accountsService.deposit(accountId, amount);
    }


}
