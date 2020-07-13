package com.scratch.tpservice.models;

public class Account {

    private String accountId;
    private double balance;
    private boolean isFrozen = false;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }

    public double getBalance(){
        return balance;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public boolean isFrozen(){
        return isFrozen;
    }

    public void setFrozen(boolean isFrozen){
        this.isFrozen = isFrozen;
    }

    @Override
    public String toString(){
        return "[ accountId = " + accountId + ", balance = " + balance + ", isFrozen = " + isFrozen + " ]";
    }
}
