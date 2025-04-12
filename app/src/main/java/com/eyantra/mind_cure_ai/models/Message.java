package com.eyantra.mind_cure_ai.models;

public class Message {
    private String messageId;
    private String senderId;
    private String content;
    private long timestamp;
    private String messageType; // "text", "image", "emotion"
    private String emotionType; // For emotion messages
    private String imageUrl; // For image messages

    // Empty constructor required for Firebase
    public Message() {}

    public Message(String messageId, String senderId, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.messageType = "text";
    }

    // Constructor for emotion messages
    public Message(String messageId, String senderId, String emotionType, String imageUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.emotionType = emotionType;
        this.imageUrl = imageUrl;
        this.timestamp = System.currentTimeMillis();
        this.messageType = "emotion";
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getEmotionType() {
        return emotionType;
    }

    public void setEmotionType(String emotionType) {
        this.emotionType = emotionType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 