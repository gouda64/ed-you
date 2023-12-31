package com.gouda.edyou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
public class Feedback {
    @Column(name = "id", unique = true)
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "comment")
    @NotNull
    @NotBlank
    private String comment;

    @Column(name = "timestamp")
    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "staff", referencedColumnName = "id")
    @NotNull
    private Staff staff;

    @Column(name = "rating")
    @NotNull
    private int rating;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
