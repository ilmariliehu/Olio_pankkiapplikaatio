package com.example.harkkaty;

public class transactions {
    private String accNmbr, note;

    public transactions (String nr, String n) {

        accNmbr = nr;
        note = n;
    }

    public String getNote() {
        return note;
    }

    public String getaNmbr() {
        return accNmbr;
    }
/*
    public void setNote(String note) {
        this.note = note;
    }

    public void setaNmbr(String accNmbr) {
        this.accNmbr = accNmbr;
    }
    */
}