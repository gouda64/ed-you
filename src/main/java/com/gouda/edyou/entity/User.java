package com.gouda.edyou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class User {
    @Column(name = "id", unique = true)
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "name")
    @NotNull
    @NotBlank
    private String name;

    @Column(name = "email", unique = true)
    @Email
    @NotNull
    @NotBlank
    private String email;

    @ManyToOne
    @JoinColumn(name = "school", referencedColumnName = "id")
    @NotNull
    private School school;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
