package com.github.espressopad.models;

public class CompletionModel {
    private String completion;
    private String shortDescription;
    private String summary;

    public CompletionModel(String completion, String shortDescription, String summary) {
        this.completion = completion;
        this.shortDescription = shortDescription;
        this.summary = summary;
    }

    public CompletionModel(String completion) {
        this.completion = completion;
    }

    public String getCompletion() {
        return this.completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
