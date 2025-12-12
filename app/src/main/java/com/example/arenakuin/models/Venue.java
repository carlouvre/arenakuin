package com.example.arenakuin.models;

import java.util.Locale;

public class Venue {
    private int venueId;
    private String venueName;
    private String venueType;
    private String location;
    private double pricePerHour;
    private String facilities;
    private String imageUrl;
    private double rating;
    private String openTime;
    private String closeTime;
    private boolean isFavorite;
    private boolean isActive; // Field untuk admin (Status Aktif/Nonaktif)

    public Venue() {
    }

    public Venue(int venueId, String venueName, String venueType, String location,
                 double pricePerHour, String facilities, String imageUrl, double rating,
                 String openTime, String closeTime) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.venueType = venueType;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.facilities = facilities;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isFavorite = false;
        this.isActive = true; // Default aktif saat dibuat manual
    }

    // Getters and Setters
    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueType() { return venueType; }
    public void setVenueType(String venueType) { this.venueType = venueType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // --- PERBAIKAN DI SINI ---
    public String getFormattedPrice() {
        // Menggunakan Locale ID agar pemisah ribuan menggunakan titik (.) bukan koma (,)
        // Contoh: Rp 120.000
        return "Rp " + String.format(new Locale("id", "ID"), "%,.0f", pricePerHour);
    }

    public String getOperatingHours() {
        return openTime + " - " + closeTime;
    }
}