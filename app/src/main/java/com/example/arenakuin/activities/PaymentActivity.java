package com.example.arenakuin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.arenakuin.R;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

public class PaymentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvVenueName, tvBookingDate, tvBookingTime;
    private TextView tvTotalPrice, tvPointsEarned;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirmPayment;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private int venueId;
    private String venueName, bookingDate, startTime, endTime;
    private double totalPrice;
    private String selectedPaymentMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        initDatabase();
        getIntentData();
        displayBookingInfo();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvVenueName = findViewById(R.id.tv_venue_name);
        tvBookingDate = findViewById(R.id.tv_booking_date);
        tvBookingTime = findViewById(R.id.tv_booking_time);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvPointsEarned = findViewById(R.id.tv_points_earned);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnConfirmPayment = findViewById(R.id.btn_confirm_payment);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
    }

    private void getIntentData() {
        venueId = getIntent().getIntExtra("venue_id", -1);
        venueName = getIntent().getStringExtra("venue_name");
        bookingDate = getIntent().getStringExtra("booking_date");
        startTime = getIntent().getStringExtra("start_time");
        endTime = getIntent().getStringExtra("end_time");
        totalPrice = getIntent().getDoubleExtra("total_price", 0);
    }

    private void displayBookingInfo() {
        tvVenueName.setText(venueName);
        tvBookingDate.setText(bookingDate);
        tvBookingTime.setText(startTime + " - " + endTime);
        tvTotalPrice.setText("Rp " + String.format("%,.0f", totalPrice));
        int pointsEarned = (int) (totalPrice * 0.01);
        tvPointsEarned.setText("Anda akan mendapat " + pointsEarned + " poin");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_ewallet) {
                selectedPaymentMethod = "E-Wallet";
            } else if (checkedId == R.id.rb_transfer) {
                selectedPaymentMethod = "Transfer Bank";
            } else if (checkedId == R.id.rb_cash) {
                selectedPaymentMethod = "Tunai";
            }
        });

        btnConfirmPayment.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Pilih metode pembayaran", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();

        long bookingId = dbHelper.createBooking(userId, venueId, bookingDate, startTime, endTime, totalPrice, selectedPaymentMethod, "Online", userId, "");

        if (bookingId != -1) {
            Toast.makeText(this, "Booking berhasil! Silakan lakukan pembayaran.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Booking gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }
}
