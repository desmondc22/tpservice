package com.scratch.tpservice.services;

import com.scratch.tpservice.TspException;
import com.scratch.tpservice.models.Account;
import com.scratch.tpservice.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountsService {

    public Map<String, Account> getAccountsDB() {
        return accountsDB;
    }

    private Map<String, Account> accountsDB = new HashMap<String, Account>();

    public void createAccount(String accountId) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBalance(0);
        accountsDB.put(accountId, account);
    }

    public boolean isFrozen(String accountId){
        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }
        Account account = accountsDB.get(accountId);
        return account.isFrozen();
    }

    public double getBalance(String accountId){
        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }
        Account account = accountsDB.get(accountId);
        return account.getBalance();
    }

    public void freezeAccount(String accountId){
        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }
        Account account = accountsDB.get(accountId);
        account.setFrozen(true);
    }

    public void thawAccount(String accountId){
        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }
        Account account = accountsDB.get(accountId);
        account.setFrozen(false);
    }

    public void deposit(String accountId, double amount) throws TspException {
        if(amount < 0){
            throw new TspException(ErrorCode.INVALID_AMOUNT);
        }

        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }
        Account account = accountsDB.get(accountId);

        if(account.isFrozen()){
            throw new TspException(ErrorCode.FROZEN_ACCOUNT);
        }

        account.setBalance(account.getBalance() + amount);
        accountsDB.put(accountId, account);
    }

    public double withdraw(String accountId, double amount) throws TspException {

        if(amount < 0){
            throw new TspException(ErrorCode.INVALID_AMOUNT);
        }

        if(!accountsDB.containsKey(accountId)) {
            createAccount(accountId);
        }

        Account account = accountsDB.get(accountId);
        if(account.isFrozen()){
            throw new TspException(ErrorCode.FROZEN_ACCOUNT);
        }

        double remainingBalance = account.getBalance() - amount;
        if(remainingBalance < 0){
            throw new TspException(ErrorCode.NEGATIVE_BALANCE);
        }

        account.setBalance(remainingBalance);
        accountsDB.put(accountId, account);
        return remainingBalance;
    }
}
