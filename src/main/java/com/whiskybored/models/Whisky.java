package com.whiskybored.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "whiskies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Whisky {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String image;

    @Column(length = 3000, nullable = false)
    private String tastingNotes;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
    })
    @JoinColumn(name = "whisky_creator")
    @JsonIgnoreProperties("whiskies")
    private AppUser user;

}
