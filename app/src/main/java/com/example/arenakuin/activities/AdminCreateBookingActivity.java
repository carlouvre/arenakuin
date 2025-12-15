package com.example.arenakuin.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arenakuin.R;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Venue;
import com.example.arenakuin.utils.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminCreateBookingActivity extends AppCompatActivity {

    // UI Components
    private Spinner spinnerVenue;
    private EditText etCustomerName, etCustomerPhone, etCustomerEmail, etNotes;
    private TextView tvSelectedDate, tvStartTime, tvEndTime, tvDuration, tvPricePerHour, tvTotalPrice;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnCreateBooking;
    private RadioGroup rgBookingType, rgPaymentMethod;

    // Database & Logic
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Venue> venueList;
    private Venue selectedVenue;

    // Selection Data
    private Calendar selectedDate;
    private int startHour = -1, startMinute = 0;
    private int endHour = -1, endMinute = 0;
    private boolean dateSelected = false;
    private boolean startTimeSelected = false;
    private boolean endTimeSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_booking);

        // --- FIX HEADER DOUBLE: Sembunyikan Action Bar bawaan ---
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        selectedDate = Calendar.getInstance();

        initViews();
        loadVenues();
        setupListeners();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Pastikan ID ini sama persis dengan yang ada di XML Anda
        spinnerVenue = findViewById(R.id.spinner_venue);
        etCustomerName = findViewById(R.id.et_customer_name);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etCustomerEmail = findViewById(R.id.et_customer_email);
        etNotes = findViewById(R.id.et_notes);

        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvPricePerHour = findViewById(R.id.tv_price_per_hour);
        tvTotalPrice = findViewById(R.id.tv_total_price);

        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectStartTime = findViewById(R.id.btn_select_start_time);
        btnSelectEndTime = findViewById(R.id.btn_select_end_time);
        btnCreateBooking = findViewById(R.id.btn_create_booking);

        rgBookingType = findViewById(R.id.rg_booking_type);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);

        // Set default radio button ke Offline jika ada
        RadioButton rbOffline = findViewById(R.id.rb_offline);
        if (rbOffline != null) rbOffline.setChecked(true);
    }

    private void loadVenues() {
        venueList = new ArrayList<>();
        List<String> venueNames = new ArrayList<>();

        // Ambil semua venue dari database
        Cursor cursor = dbHelper.getAllVenues();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int venueId = cursor.getInt(cursor.getColumnIndexOrThrow("venue_id"));
                String venueName = cursor.getString(cursor.getColumnIndexOrThrow("venue_name"));
                String venueType = cursor.getString(cursor.getColumnIndexOrThrow("venue_type"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price_per_hour"));
                String facilities = cursor.getString(cursor.getColumnIndexOrThrow("facilities"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"));
                String openTime = cursor.getString(cursor.getColumnIndexOrThrow("open_time"));
                String closeTime = cursor.getString(cursor.getColumnIndexOrThrow("close_time"));

                Venue venue = new Venue(venueId, venueName, venueType, location, price,
                        facilities, imageUrl, rating, openTime, closeTime);
                venueList.add(venue);
                venueNames.add(venueName + " (" + venueType + ")");
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (spinnerVenue != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    venueNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerVenue.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        if (spinnerVenue != null) {
            spinnerVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 && position < venueList.size()) {
                        selectedVenue = venueList.get(position);
                        calculatePrice();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectStartTime.setOnClickListener(v -> showStartTimePicker());
        btnSelectEndTime.setOnClickListener(v -> showEndTimePicker());
        btnCreateBooking.setOnClickListener(v -> createBooking());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    dateSelected = true;
                    updateSelectedDate();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showStartTimePicker() {
        if (!dateSelected) {
            Toast.makeText(this, "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int openHour = 8;
        if (selectedVenue != null) {
            try {
                String openTimeStr = selectedVenue.getOpenTime().replace(".", ":");
                openHour = Integer.parseInt(openTimeStr.split(":")[0]);
            } catch (Exception e) {
                Log.e("TimePicker", "Error parsing time: " + e.getMessage());
                openHour = 8;
            }
        }
        final int finalOpenHour = openHour;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay < finalOpenHour) {
                        Toast.makeText(this, "Venue belum buka jam segini", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startHour = hourOfDay;
                    startMinute = 0;
                    startTimeSelected = true;
                    updateStartTime();
                    calculatePrice();
                },
                startHour > -1 ? startHour : openHour, 0, true
        );
        timePickerDialog.show();
    }

    private void showEndTimePicker() {
        if (!startTimeSelected) {
            Toast.makeText(this, "Pilih jam mulai terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (hourOfDay <= startHour) {
                        Toast.makeText(this, "Jam selesai harus setelah jam mulai", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    endHour = hourOfDay;
                    endMinute = 0;
                    endTimeSelected = true;
                    updateEndTime();
                    calculatePrice();
                },
                startHour + 1, 0, true
        );
        timePickerDialog.show();
    }

    private void updateSelectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvSelectedDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void updateStartTime() {
        tvStartTime.setText(String.format(Locale.getDefault(), "%02d:00", startHour));
    }

    private void updateEndTime() {
        tvEndTime.setText(String.format(Locale.getDefault(), "%02d:00", endHour));
    }

    private void calculatePrice() {
        if (!startTimeSelected || !endTimeSelected || selectedVenue == null) {
            tvTotalPrice.setText("Rp 0");
            return;
        }

        int durationHours = endHour - startHour;
        if (durationHours <= 0) {
            endTimeSelected = false;
            tvEndTime.setText("--:--");
            return;
        }

        double totalPrice = durationHours * selectedVenue.getPricePerHour();

        if (tvDuration != null) tvDuration.setText(durationHours + " Jam");

        if (tvPricePerHour != null) {
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            tvPricePerHour.setText(formatRupiah.format(selectedVenue.getPricePerHour()) + "/jam");
        }

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvTotalPrice.setText(formatRupiah.format(totalPrice));
    }

    private void createBooking() {
        if (etCustomerName.getText().toString().trim().isEmpty()) {
            etCustomerName.setError("Wajib diisi");
            return;
        }
        if (etCustomerPhone.getText().toString().trim().isEmpty()) {
            etCustomerPhone.setError("Wajib diisi");
            return;
        }
        if (!dateSelected || !startTimeSelected || !endTimeSelected) {
            Toast.makeText(this, "Lengkapi jadwal booking", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedVenue == null) {
            Toast.makeText(this, "Pilih venue", Toast.LENGTH_SHORT).show();
            return;
        }

        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();

        String notes = "Walk-in: " + customerName + " (" + customerPhone + ")";
        if (etNotes != null && !etNotes.getText().toString().isEmpty()) {
            notes += "\nCatatan: " + etNotes.getText().toString();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String bookingDate = dateFormat.format(selectedDate.getTime());
        String strStartTime = String.format(Locale.getDefault(), "%02d:00", startHour);
        String strEndTime = String.format(Locale.getDefault(), "%02d:00", endHour);

        // Cek Ketersediaan
        Cursor cursor = dbHelper.checkAvailability(selectedVenue.getVenueId(), bookingDate, strStartTime, strEndTime);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(this, "Jadwal bentrok! Pilih jam lain.", Toast.LENGTH_LONG).show();
            return;
        }

        int userId = createOrGetGuestUser(customerName, "", customerPhone);

        int duration = endHour - startHour;
        double total = duration * selectedVenue.getPricePerHour();

        // Get Payment Method
        String paymentMethod = "Tunai";
        if (rgPaymentMethod != null) {
            int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                if (rb != null) paymentMethod = rb.getText().toString();
            }
        }

        // Create Booking
        long bookingId = dbHelper.createBooking(
                userId,
                selectedVenue.getVenueId(),
                bookingDate,
                strStartTime,
                strEndTime,
                total,
                paymentMethod,
                "Offline", // Booking Type
                sessionManager.getUserId(),
                notes
        );

        if (bookingId != -1) {
            dbHelper.updateBookingStatus((int) bookingId, "Confirmed");
            dbHelper.updatePaymentStatus((int) bookingId, "Paid");

            Toast.makeText(this, "Booking berhasil dibuat!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan booking", Toast.LENGTH_SHORT).show();
        }
    }

    private int createOrGetGuestUser(String name, String email, String phone) {
        if (email.isEmpty()) {
            email = "guest_" + System.currentTimeMillis() + "@arenaku.in";
        }

        Cursor cursor = dbHelper.loginUser(email, "guest123");
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            cursor.close();
            return userId;
        }

        long id = dbHelper.registerCustomer(name, email, "guest123", phone);
        return (int) id;
    }
}