package com.github.vdemeester.miniature.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {

    private final Integer id;
    private final String name;
    private final Boolean completed;
    private final LocalDateTime localDateTime;

    public Todo(Integer id, String name, Boolean completed, LocalDateTime localDateTime) {
        this.id = id;
        this.name = name;
        this.completed = completed;
        this.localDateTime = localDateTime;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
