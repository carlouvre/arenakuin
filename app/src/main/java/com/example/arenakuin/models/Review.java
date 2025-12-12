package com.example.arenakuin.models;

public class Review {
    private int reviewId;
    private int userId;
    private String userName;
    private int venueId;
    private float rating;
    private String comment;
    private String reviewDate;

    public Review() {
    }

    public Review(int reviewId, int userId, String userName, int venueId,
                  float rating, String comment, String reviewDate) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.venueId = venueId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
}