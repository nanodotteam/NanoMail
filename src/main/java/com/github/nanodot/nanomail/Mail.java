package com.github.nanodot.nanomail;

import java.util.UUID;

public class Mail {
    public int mailID;
    public UUID authorUUID;
    public String content;

    public int getMailID() {
        return mailID;
    }

    public void setMailID(int mailID) {
        this.mailID = mailID;
    }

    public UUID getAuthorUUID() {
        return authorUUID;
    }

    public void setAuthorUUID(UUID authorUUID) {
        this.authorUUID = authorUUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        return "#" +
                mailID +
                ": " +
                "Mail by " +
                authorUUID.toString() +
                ":\n" +
                content;
    }
}
