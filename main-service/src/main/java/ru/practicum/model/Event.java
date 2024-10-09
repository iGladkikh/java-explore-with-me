package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import ru.practicum.model.enums.EventState;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_at")
    private Instant createdOn;
    private String description;
    @Column(name = "event_date")
    private Instant eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @OneToOne
    @JoinColumn(name = "location_id")
    Location location;
    @Column(name = "is_paid")
    Boolean paid;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "published_at")
    Instant published;
    @Column(name = "is_moderation_required")
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Formula(value = "(SELECT COUNT(r.id) FROM requests AS r WHERE r.event_id = id AND r.state = 'CONFIRMED')")
    private Integer confirmedRequests;
    @Transient
    private Long views;
}
