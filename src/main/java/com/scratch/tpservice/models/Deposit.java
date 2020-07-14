package com.scratch.tpservice.models;

public class Deposit extends Transaction{

    /* variables specific to this transaction */
    private String accountId;
    private double amount;

    // added it for JUnit testing
    public Deposit(){
        super("DEPOSIT");
    }

    public Deposit(String cmd){
        super(cmd);
    }

    public String getAccountId(){
        return accountId;
    }

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
