package io.gabbloquet.functioncalling.domaine.modele;

import java.time.LocalDateTime;
import java.util.Objects;

public class Tache {
    private final String title;
    private final LocalDateTime datetime;

    public Tache(String title, LocalDateTime datetime) {
        this.title = title;
        this.datetime = datetime;
    }

    public String getTitle() { return title; }
    public LocalDateTime getDatetime() { return datetime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tache tache = (Tache) o;
        return Objects.equals(title, tache.title) && Objects.equals(datetime, tache.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, datetime);
    }
}
