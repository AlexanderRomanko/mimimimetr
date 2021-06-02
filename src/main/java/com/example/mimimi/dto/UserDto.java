package com.example.mimimi.dto;

import com.example.mimimi.entity.Role;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public class UserDto {

    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
