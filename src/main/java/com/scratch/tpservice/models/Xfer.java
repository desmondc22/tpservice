package com.scratch.tpservice.models;

public class Xfer extends Transaction{

    /* variables specific to this transaction */
    private String fromId;
    private String toId;
    private double amount;

    // added it for JUnit testing
    public Xfer(){
        super("XFER");
    }

    public Xfer(String cmd){
        super(cmd);
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }

    public String getFromId(){
        return fromId;
    }

    public void setFromId(String fromAccountId){
        this.fromId = fromAccountId;
    }

    public String getToId(){
        return toId;
    }

    public void setToId(String toAccountId){
        this.toId = toAccountId;
    }

}
