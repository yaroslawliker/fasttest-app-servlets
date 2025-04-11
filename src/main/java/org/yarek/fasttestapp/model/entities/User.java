package org.yarek.fasttestapp.model.entities;

/**
 * Class represents a user of the application
 */

public class User {
    private String id;
    private String username;
    private String password;
    private Role role;

    public User() {
        id = null;
        username = "noname";
        password = "password";
        role = Role.USER;
    }

    public enum Role {
        USER, TEACHER, ADMIN
    }

    // -----
    // Getters and setters
    // -----

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
