package com.example.b2bouz;

public class Message {
    private String messageId;
    private String messageText;
    private String senderId;
    private String receiverId;

    private String username;

    public Message() {
        // Boş parametreli yapıcı metot
    }

    public Message(String messageId, String messageText, String senderId, String receiverId) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
