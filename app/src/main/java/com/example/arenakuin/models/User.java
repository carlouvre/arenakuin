package com.example.arenakuin.models;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private int points;
    private String membershipType;

    public User() {
    }

    public User(int userId, String name, String email, String phone,
                int points, String membershipType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.points = points;
        this.membershipType = membershipType;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
}
