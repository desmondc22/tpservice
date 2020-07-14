package com.scratch.tpservice.models;

public class Freeze extends Transaction{

    /* variables specific to this transaction */
    private String accountId;

    // added it for JUnit testing
    public Freeze(){
        super("FREEZE");
    }

    public Freeze(String cmd){
        super(cmd);
    }

    public String getAccountId(){
        return accountId;
    }

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }
}
