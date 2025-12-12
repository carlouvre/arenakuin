package com.example.arenakuin.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arenakuin.R;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    // UI Components
    private TextView tvVenueName, tvSelectedDate, tvStartTime, tvEndTime;
    private TextView tvDuration, tvPricePerHour, tvTotalPrice;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnConfirmBooking;
    private ImageButton btnBack;

    // Database & Helper
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Data Venue
    private int venueId;
    private String venueName;
    private double pricePerHour;
    private String openTime;

    // Data Pilihan User (Dideklarasikan di sini agar bisa diakses Lambda)
    private Calendar selectedDate;
    private int startHour = -1;
    private int startMinute = 0; // Default 0
    private int endHour = -1;
    private int endMinute = 0;   // Default 0

    // Flag status pemilihan
    private boolean isDateSelected = false;
    private boolean isStartTimeSelected = false;
    private boolean isEndTimeSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. Inisialisasi Database & Helper
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        selectedDate = Calendar.getInstance();

        // 2. Inisialisasi Views
        initViews();

        // 3. Ambil Data Intent & Validasi
        if (!getIntentData()) {
            Toast.makeText(this, "Data venue tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4. Setup Tampilan Awal & Listeners
        setupUI();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvVenueName = findViewById(R.id.tv_venue_name);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvPricePerHour = findViewById(R.id.tv_price_per_hour);
        tvTotalPrice = findViewById(R.id.tv_total_price);

        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectStartTime = findViewById(R.id.btn_select_start_time);
        btnSelectEndTime = findViewById(R.id.btn_select_end_time);
        btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
    }

    private boolean getIntentData() {
        if (getIntent() != null) {
            venueId = getIntent().getIntExtra("venue_id", -1);
            venueName = getIntent().getStringExtra("venue_name");
            pricePerHour = getIntent().getDoubleExtra("price_per_hour", 0);
            openTime = getIntent().getStringExtra("open_time");
            // closeTime tidak perlu disimpan jika tidak validasinya kompleks
            return venueId != -1;
        }
        return false;
    }

    private void setupUI() {
        tvVenueName.setText(venueName);

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvPricePerHour.setText(formatRupiah.format(pricePerHour) + "/jam");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectStartTime.setOnClickListener(v -> showStartTimePicker());
        btnSelectEndTime.setOnClickListener(v -> showEndTimePicker());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    isDateSelected = true;

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
                    tvSelectedDate.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        if (!isDateSelected) {
            Toast.makeText(this, "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse jam buka (default jam 8 jika format salah/null)
        int openHourInt = 8;
        try {
            if (openTime != null && openTime.contains(":")) {
                openHourInt = Integer.parseInt(openTime.split(":")[0]);
            } else if (openTime != null && !openTime.contains(":")) {
                // antisipasi format "08.00"
                openHourInt = Integer.parseInt(openTime.split("\\.")[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Gunakan variabel final lokal untuk lambda jika diperlukan (meski di Java 8+ field class bisa akses lgsg)
        final int finalOpenHour = openHourInt;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay < finalOpenHour) {
                        Toast.makeText(this, "Venue belum buka jam segini (Buka: " + finalOpenHour + ":00)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startHour = hourOfDay;
                    startMinute = 0; // Paksa menit 0 (booking per jam)
                    isStartTimeSelected = true;

                    tvStartTime.setText(String.format(Locale.getDefault(), "%02d:00", startHour));

                    // Reset end time jika tidak valid
                    if (isEndTimeSelected && endHour <= startHour) {
                        isEndTimeSelected = false;
                        tvEndTime.setText("--:--");
                        tvTotalPrice.setText("Rp 0");
                        tvDuration.setText("0 Jam");
                    } else {
                        calculatePrice();
                    }
                },
                startHour > -1 ? startHour : finalOpenHour,
                0,
                true // 24 hour format
        );
        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        if (!isStartTimeSelected) {
            Toast.makeText(this, "Pilih waktu mulai terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay <= startHour) {
                        Toast.makeText(this, "Waktu selesai harus setelah waktu mulai", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    endHour = hourOfDay;
                    endMinute = 0;
                    isEndTimeSelected = true;

                    tvEndTime.setText(String.format(Locale.getDefault(), "%02d:00", endHour));
                    calculatePrice();
                },
                startHour + 1, // Default jam selesai = jam mulai + 1
                0,
                true
        );
        timePickerDialog.show();
    }

    private void calculatePrice() {
        if (!isStartTimeSelected || !isEndTimeSelected) return;

        int duration = endHour - startHour;
        if (duration <= 0) {
            tvTotalPrice.setText("Durasi tidak valid");
            return;
        }

        double totalPrice = duration * pricePerHour;

        tvDuration.setText(duration + " Jam");

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotalPrice.setText(formatRupiah.format(totalPrice));
    }

    private void confirmBooking() {
        // Cek Login Session sebelum konfirmasi
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Silakan login terlebih dahulu untuk booking", Toast.LENGTH_SHORT).show();
            // Opsional: Arahkan ke LoginActivity
            return;
        }

        if (!isDateSelected || !isStartTimeSelected || !isEndTimeSelected) {
            Toast.makeText(this, "Lengkapi semua data booking", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String bookingDate = dateFormat.format(selectedDate.getTime());
        String strStartTime = String.format(Locale.getDefault(), "%02d:00", startHour);
        String strEndTime = String.format(Locale.getDefault(), "%02d:00", endHour);

        // Cek ketersediaan di database (pastikan method checkAvailability ada & benar di DB Helper)
        Cursor cursor = dbHelper.checkAvailability(venueId, bookingDate, strStartTime, strEndTime);
        boolean isAvailable = true;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isAvailable = false;
            }
            cursor.close();
        }

        if (!isAvailable) {
            Toast.makeText(this, "Slot waktu tidak tersedia. Pilih waktu lain.", Toast.LENGTH_LONG).show();
            return;
        }

        int duration = endHour - startHour;
        double totalPrice = duration * pricePerHour;

        // Pindah ke PaymentActivity
        Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
        intent.putExtra("venue_id", venueId);
        intent.putExtra("venue_name", venueName);
        intent.putExtra("booking_date", bookingDate);
        intent.putExtra("start_time", strStartTime);
        intent.putExtra("end_time", strEndTime);
        intent.putExtra("total_price", totalPrice);
        startActivity(intent);
    }
}