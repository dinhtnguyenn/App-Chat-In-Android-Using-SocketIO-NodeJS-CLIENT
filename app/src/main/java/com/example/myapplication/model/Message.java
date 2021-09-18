package com.example.myapplication.model;

public class Message {
    private String Content;
    private String Receiver;

    public Message(String content, String receiver) {
        Content = content;
        Receiver = receiver;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }
}
