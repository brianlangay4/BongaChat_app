package com.bongachat;

public class Message {
    private String senderName;
    private String messageContent;
    private long timestamp; // You can use a timestamp to display when the message was sent

    public Message(String senderName, String messageContent, long timestamp) {
        this.senderName = senderName;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

