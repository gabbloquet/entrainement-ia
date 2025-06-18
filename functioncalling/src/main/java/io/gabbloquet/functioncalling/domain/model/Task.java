package io.gabbloquet.functioncalling.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String title;
    private final LocalDateTime datetime;

    public Task(String title, LocalDateTime datetime) {
        this.title = title;
        this.datetime = datetime;
    }

    public String getTitle() { return title; }
    public LocalDateTime getDatetime() { return datetime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(datetime, task.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, datetime);
    }
}
