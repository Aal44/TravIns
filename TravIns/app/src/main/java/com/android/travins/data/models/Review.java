package com.android.travins.data.models;

public class Review {
    private String author;
    private String text;
    private String rate;

    public Review(String author, String text, String rate) {
        this.author = author;
        this.text = text;
        this.rate = rate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
