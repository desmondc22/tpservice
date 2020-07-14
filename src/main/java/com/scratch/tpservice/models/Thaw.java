package com.scratch.tpservice.models;

public class Thaw extends Transaction{

    /* variables specific to this transaction */
    private String accountId;

    // added it for JUnit testing
    public Thaw(){
        super("THAW");
    }

    public Thaw(String cmd){
        super(cmd);
    }

    public String getAccountId(){
        return accountId;
    }

    public void setAccountId(String accountId){
        this.accountId = accountId;
    }
}
