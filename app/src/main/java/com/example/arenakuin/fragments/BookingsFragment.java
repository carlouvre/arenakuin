package com.example.arenakuin.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.adapters.BookingAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Booking;
import com.example.arenakuin.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment {

    private RecyclerView rvBookings;
    private TextView tvEmptyState;

    private BookingAdapter bookingAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<Booking> bookingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        initViews(view);
        initDatabase();
        loadBookings();

        return view;
    }

    private void initViews(View view) {
        rvBookings = view.findViewById(R.id.rv_bookings);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    private void loadBookings() {
        bookingList = new ArrayList<>();

        int userId = sessionManager.getUserId();
        Cursor cursor = dbHelper.getUserBookings(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookingList.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (bookingList.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            bookingAdapter = new BookingAdapter(requireContext(), bookingList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            rvBookings.setLayoutManager(layoutManager);
            rvBookings.setAdapter(bookingAdapter);
        }
    }

    private Booking createBookingFromCursor(Cursor cursor) {
        int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("booking_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        int venueId = cursor.getInt(cursor.getColumnIndexOrThrow("venue_id"));
        String venueName = cursor.getString(cursor.getColumnIndexOrThrow("venue_name"));
        String venueType = cursor.getString(cursor.getColumnIndexOrThrow("venue_type"));
        String bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
        double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
        
        // Dapatkan nama pengguna dari sesi, karena ini adalah fragmen customer
        String userName = sessionManager.getUserName();

        // Gunakan konstruktor yang benar
        return new Booking(bookingId, userId, venueId, venueName, venueType,
                bookingDate, startTime, endTime, totalPrice, status,
                paymentMethod, userName);
    }
}
