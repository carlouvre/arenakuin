package com.example.arenakuin.fragments.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.adapters.AdminPaymentAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class AdminPaymentsFragment extends Fragment {

    private RecyclerView rvPendingPayments;
    private android.view.View tvEmptyState;

    private AdminPaymentAdapter paymentAdapter;
    private DatabaseHelper dbHelper;

    private List<Booking> pendingPayments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_payments, container, false);

        initViews(view);
        initDatabase();
        loadPendingPayments();

        return view;
    }

    private void initViews(View view) {
        rvPendingPayments = view.findViewById(R.id.rv_pending_payments);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
    }

    private void loadPendingPayments() {
        pendingPayments = new ArrayList<>();

        Cursor cursor = dbHelper.getPendingPayments();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                pendingPayments.add(booking);
            } while (cursor.moveToNext());
            cursor.close();
        }

        displayPayments();
    }

    private void displayPayments() {
        if (pendingPayments.isEmpty()) {
            rvPendingPayments.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvPendingPayments.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            paymentAdapter = new AdminPaymentAdapter(requireContext(), pendingPayments);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            rvPendingPayments.setLayoutManager(layoutManager);
            rvPendingPayments.setAdapter(paymentAdapter);

            paymentAdapter.setOnPaymentActionListener(new AdminPaymentAdapter.OnPaymentActionListener() {
                @Override
                public void onConfirmPayment(Booking booking) {
                    confirmPayment(booking.getBookingId());
                }

                @Override
                public void onRejectPayment(Booking booking) {
                    rejectPayment(booking.getBookingId());
                }
            });
        }
    }

    private Booking createBookingFromCursor(Cursor cursor) {
        int bookingId = cursor.getInt(cursor.getColumnIndexOrThrow("booking_id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        int venueId = cursor.getInt(cursor.getColumnIndexOrThrow("venue_id"));
        String venueName = cursor.getString(cursor.getColumnIndexOrThrow("venue_name"));
        String customerName = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
        String bookingDate = cursor.getString(cursor.getColumnIndexOrThrow("booking_date"));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time"));
        double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));
        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        String paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

        Booking booking = new Booking(bookingId, userId, venueId, venueName, "",
                bookingDate, startTime, endTime, totalPrice, status,
                paymentMethod, createdAt);
        booking.setCustomerName(customerName);

        return booking;
    }

    private void confirmPayment(int bookingId) {
        boolean success = dbHelper.updatePaymentStatus(bookingId, "Paid");
        if (success) {
            Toast.makeText(requireContext(),
                    "Pembayaran dikonfirmasi",
                    Toast.LENGTH_SHORT).show();
            loadPendingPayments(); // Refresh
        } else {
            Toast.makeText(requireContext(),
                    "Gagal konfirmasi pembayaran",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void rejectPayment(int bookingId) {
        // You can add a "Rejected" status or cancel the booking
        boolean success = dbHelper.updateBookingStatus(bookingId, "Cancelled");
        if (success) {
            Toast.makeText(requireContext(),
                    "Booking dibatalkan",
                    Toast.LENGTH_SHORT).show();
            loadPendingPayments(); // Refresh
        }
    }
}