package com.example.android.vozmail.api.model;

import com.fasterxml.jackson.core.JsonPointer;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.commons.codec.language.Soundex;

public class inboxMails {
    private String date;
    private String from;
    private ArrayList<String> labels;
    private String messageID;
    private String snippet;
    private Boolean star = false;
    private String subject;
    private String text;

    /* renamed from: to */
    private String to;

    public inboxMails(String mFrom, String mTo, String mSubject, String mDate, String mSnippet, String mMesaageID, String mtext, ArrayList<String> mlabels) {
        this.from = mFrom;
        this.subject = mSubject;
        this.date = mDate;
        this.snippet = mSnippet;
        this.messageID = mMesaageID;
        this.to = mTo;
        this.text = mtext;
        this.labels = mlabels;
        if (mlabels.contains("STARRED")) {
            this.star = true;
        }
    }



    public String getFrom()
    {

        return this.from;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getDate() {
        return this.date;
    }

    public String getSnippet() {
        return this.snippet;
    }

    public boolean getStar() {
        return this.star.booleanValue();
    }

    public String getTo() {
        return this.to;
    }

    public String getText() {
        return decodeString(this.text);
    }

    public String getMessageID() {
        return this.messageID;
    }

    public void setDate(String date2) {
        String mdate[] = date2.split(" ");
        this.date = mdate[0]+" "+mdate[1];

    }

    public void setFrom(String from2) {

        String[] from_v = from2.split("<");

        this.from = from_v[0];
    }

    public void setSnippet(String snippet2) {
        this.snippet = snippet2;
    }

    public void setMessageID(String messageID2) {
        this.messageID = messageID2;
    }

    public void setSubject(String subject2) {
        this.subject = subject2;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setStar(boolean star2) {
        this.star = Boolean.valueOf(star2);
    }

    /* access modifiers changed from: package-private */
    public String decodeString(String encodedString) {
        encodedString.replace(Soundex.SILENT_MARKER, '+').replace('_', JsonPointer.SEPARATOR);
        return new String(Base64.getDecoder().decode(encodedString));
    }
}