package com.eyantra.mind_cure_ai;

public class Tip {
    private String category;
    private String text;

    public Tip(String category, String text) {
        this.category = category;
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public String getText() {
        return text;
    }
} 