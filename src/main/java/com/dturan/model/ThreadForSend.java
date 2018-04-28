package com.dturan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ThreadForSend {
    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getVotes() {
        return votes;
    }

    public void setVotes(BigDecimal votes) {
        this.votes = votes;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @JsonProperty("id")
    private BigDecimal id = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("author")
    private String author = null;

    @JsonProperty("forum")
    private String forum = null;

    @JsonProperty("message")
    private String message = null;

    @JsonProperty("votes")
    private BigDecimal votes = null;

    @JsonProperty("created")
    private String created = null;

    public ThreadForSend(Thread thread) {
        this.setId(thread.getId());
        this.setTitle(thread.getTitle());
        this.setAuthor(thread.getAuthor());
        this.setForum(thread.getForum());
        this.setMessage(thread.getMessage());
        this.setVotes(thread.getVotes());
        this.setCreated(thread.getCreated());
    }
}
