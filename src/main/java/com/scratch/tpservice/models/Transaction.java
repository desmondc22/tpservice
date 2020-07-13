package com.scratch.tpservice.models;

public class Transaction {

    private String cmd;

    public Transaction(String cmd){
        super();
        this.cmd = cmd;
    }

    public String getCmd(){
        return cmd;
    }

    public void setCmd(String cmd){
        this.cmd = cmd;
    }

    @Override
    public String toString(){
        return "[ Cmd:" + cmd + " ]";
    }
}
