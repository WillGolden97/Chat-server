/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.bean;

import java.io.Serializable;

/**
 *
 * @author William
 */
public class Message implements Serializable {

    private String message;
    private String To;
    private String from;
    private String date;

    public Message(String message, String From, String To) {
        this.message = message;
        this.from = From;
        this.To = To;
    }

    public Message() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String nickNameFrom) {
        this.from = nickNameFrom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String contactName) {
        this.To = contactName;
    }

}
