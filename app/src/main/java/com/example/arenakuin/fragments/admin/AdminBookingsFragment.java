package com.example.arenakuin.fragments.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.adapters.AdminBookingAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class AdminBookingsFragment extends Fragment {

    private Spinner spinnerFilter;
    private RecyclerView rvBookings;
    private TextView tvEmptyState;

    private AdminBookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;

    private List<Booking> bookingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_bookings, container, false);

        initViews(view);
        initDatabase();
        setupFilterSpinner();
        loadAllBookings();

        return view;
    }

    private void initViews(View view) {
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        rvBookings = view.findViewById(R.id.rv_admin_bookings);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
    }

    private void setupFilterSpinner() {
        String[] filters = {"Semua", "Pending", "Confirmed", "Completed", "Cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filters
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filters[position];
                if ("Semua".equals(selectedFilter)) {
                    loadAllBookings();
                } else {
                    loadBookingsByStatus(selectedFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAllBookings() {
        bookingList = new ArrayList<>();

        Cursor cursor = dbHelper.getAllBookings();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookingList.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }

        displayBookings();
    }

    private void loadBookingsByStatus(String status) {
        bookingList = new ArrayList<>();

        Cursor cursor = dbHelper.getBookingsByStatus(status);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookingList.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }

        displayBookings();
    }

    private void displayBookings() {
        if (bookingList.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            bookingAdapter = new AdminBookingAdapter(requireContext(), bookingList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            rvBookings.setLayoutManager(layoutManager);
            rvBookings.setAdapter(bookingAdapter);

            // Set click listener for booking actions
            bookingAdapter.setOnBookingActionListener(new AdminBookingAdapter.OnBookingActionListener() {
                @Override
                public void onUpdateStatus(Booking booking, String newStatus) {
                    updateBookingStatus(booking.getBookingId(), newStatus);
                }

                @Override
                public void onViewDetails(Booking booking) {
                    showBookingDetails(booking);
                }
            });
        }
    }

    private Booking createBookingFromCursor(Cursor cursor) {
        int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("booking_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        int venueId = cursor.getInt(cursor.getColumnIndexOrThrow("venue_id"));
        String venueName = cursor.getString(cursor.getColumnIndexOrThrow("venue_name"));
        String venueType = cursor.getString(cursor.getColumnIndexOrThrow("venue_type"));
        String customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
        String bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
        double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String paymentStatus = cursor.getString(cursor.getColumnIndexOrThrow("payment_status"));
        String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
        String bookingType = cursor.getString(cursor.getColumnIndexOrThrow("booking_type"));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

        Booking booking = new Booking(bookingId, userId, venueId, venueName, venueType,
                bookingDate, startTime, endTime, totalPrice, status,
                paymentMethod, createdAt);
        booking.setCustomerName(customerName);
        booking.setPaymentStatus(paymentStatus);
        booking.setBookingType(bookingType);

        return booking;
    }

    private void updateBookingStatus(int bookingId, String newStatus) {
        boolean success = dbHelper.updateBookingStatus(bookingId, newStatus);
        if (success) {
            android.widget.Toast.makeText(requireContext(),
                    "Status booking berhasil diupdate",
                    android.widget.Toast.LENGTH_SHORT).show();
            loadAllBookings(); // Refresh list
        } else {
            android.widget.Toast.makeText(requireContext(),
                    "Gagal update status",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingDetails(Booking booking) {
        // TODO: Show booking details dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Detail Booking")
                .setMessage(
                        "ID: #" + booking.getBookingId() + "\n" +
                                "Customer: " + booking.getCustomerName() + "\n" +
                                "Venue: " + booking.getVenueName() + "\n" +
                                "Tanggal: " + booking.getBookingDate() + "\n" +
                                "Waktu: " + booking.getDuration() + "\n" +
                                "Total: " + booking.getFormattedPrice() + "\n" +
                                "Status: " + booking.getStatus() + "\n" +
                                "Payment: " + booking.getPaymentStatus() + "\n" +
                                "Tipe: " + booking.getBookingType()
                )
                .setPositiveButton("OK", null)
                .show();
    }
}