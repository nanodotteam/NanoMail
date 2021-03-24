package com.github.nanodot.nanomail;

import java.util.LinkedList;

public class MailBox {
    // TODO: Refactor needed! mails should be HashMap<Integer, Mail> to store mail-id and corresponding mail.
    // TODO: Refactor needed! It is needed to change variable accessibility and remove default constructor
    // In other words, mail shouldn't have mailID field
    public int lastMailID;
    public LinkedList<Mail> mails;

    public int getLastMailID() {
        return lastMailID;
    }

    public void setLastMailID(int lastMailID) {
        this.lastMailID = lastMailID;
    }

    public LinkedList<Mail> getMails() {
        return mails;
    }

    public void setMails(LinkedList<Mail> mails) {
        this.mails = mails;
    }

    public String toString() {
        StringBuilder response = new StringBuilder();
        response.append("Last Mail ID: ").append(lastMailID).append("\n");
        for(Mail m : mails) {
            response.append(m.toString()).append("\n");
        }
        return response.toString();
    }
}
