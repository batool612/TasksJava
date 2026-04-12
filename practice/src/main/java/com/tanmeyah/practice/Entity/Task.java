package com.tanmeyah.practice.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "title_en")
    private String titleEn;

    @Size(max = 255)
    @Column(name = "title_ar")
    private String titleAr;

    @Size(max = 1000)
    @Column(name = "description_en")
    private String descriptionEn;

    @Size(max = 1000)
    @Column(name = "description_ar")
    private String descriptionAr;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}