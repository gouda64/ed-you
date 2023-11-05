package com.gouda.edyou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Entity
public class Staff {
    @Column(name = "id", unique = true)
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "name")
    @NotNull
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "school", referencedColumnName = "id")
    @NotNull
    private School school;

    @Column(name = "feedback")
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Feedback> feedback;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Set<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(Set<Feedback> feedback) {
        this.feedback = feedback;
    }
}
