package com.eyantra.mind_cure_ai;

public class Goal {
    private String title;
    private String description;
    private long deadline;
    private boolean isCompleted;

    public Goal(String title, String description, long deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isCompleted = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
} 