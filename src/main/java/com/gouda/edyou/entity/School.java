package com.gouda.edyou.entity;

import com.gouda.edyou.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Entity
public class School {
    @Column(name = "id", unique = true)
    @Id
    private String code = generateCode();

    @Column(name = "name")
    @NotNull
    @NotBlank
    private String name;

    @Column(name = "admin")
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> admins;

    @Column(name = "staff")
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Staff> staff;

    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[6];
        random.nextBytes(randomBytes);
        String code = Base64.getEncoder().encodeToString(randomBytes);
        return code.substring(0, code.length()-2); //to get rid of padding
    }

    public String getCode() {
        return code.toString();
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    public Set<Staff> getStaff() {
        return staff;
    }

    public void setStaff(Set<Staff> staff) {
        this.staff = staff;
    }
}
