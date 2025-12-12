package com.example.arenakuin.models;

public class Booking {
    private int bookingId;
    private int userId;
    private int venueId;
    private String venueName;
    private String venueType;
    private String customerName; // NEW: for admin view
    private String bookingDate;
    private String startTime;
    private String endTime;
    private double totalPrice;
    private String status;
    private String paymentStatus; // NEW: Unpaid, Paid, Refunded
    private String paymentMethod;
    private String bookingType; // NEW: Online, Offline
    private String notes; // NEW: admin notes
    private String createdAt;

    public Booking() {
    }

    public Booking(int bookingId, int userId, int venueId, String venueName,
                   String venueType, String bookingDate, String startTime,
                   String endTime, double totalPrice, String status,
                   String paymentMethod, String createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.venueId = venueId;
        this.venueName = venueName;
        this.venueType = venueType;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.paymentStatus = "Unpaid";
        this.bookingType = "Online";
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueType() { return venueType; }
    public void setVenueType(String venueType) { this.venueType = venueType; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBookingType() { return bookingType; }
    public void setBookingType(String bookingType) { this.bookingType = bookingType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getFormattedPrice() {
        return "Rp " + String.format("%,.0f", totalPrice);
    }

    public String getDuration() {
        return startTime + " - " + endTime;
    }
}
