package com.eyantra.mind_cure_ai;

public class ChatMessage {
    private String text;
    private boolean isUserMessage;

    public ChatMessage(String text, boolean isUserMessage) {
        this.text = text;
        this.isUserMessage = isUserMessage;
    }

    public String getText() {
        return text;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}
