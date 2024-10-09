package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @ManyToMany
    @JoinTable(name = "compilation_event",
            joinColumns = {@JoinColumn(name = "compilation_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id", referencedColumnName = "id")}
    )
    private List<Event> events;
    @Column(name = "is_pinned")
    private Boolean pinned;
}