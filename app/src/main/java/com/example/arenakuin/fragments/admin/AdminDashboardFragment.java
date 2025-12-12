package com.example.arenakuin.fragments.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.arenakuin.R;
import com.example.arenakuin.activities.AdminCreateBookingActivity;
import com.example.arenakuin.activities.LoginActivity;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

public class AdminDashboardFragment extends Fragment {

    private TextView tvWelcomeAdmin, tvTotalBookings, tvTodayBookings;
    private TextView tvTodayRevenue, tvPendingPayments;

    // CardView untuk statistik (tetap CardView)
    private CardView cardTodayBookings, cardPendingPayments, cardRevenue, cardTotalBookings;

    // REVISI: Menggunakan Button, bukan CardView (sesuai XML baru)
    private Button btnCreateBooking;
    private Button btnLogout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        initViews(view);
        initDatabase();
        loadDashboardData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        tvWelcomeAdmin = view.findViewById(R.id.tv_welcome_admin);
        tvTotalBookings = view.findViewById(R.id.tv_total_bookings);
        tvTodayBookings = view.findViewById(R.id.tv_today_bookings);
        tvTodayRevenue = view.findViewById(R.id.tv_today_revenue);
        tvPendingPayments = view.findViewById(R.id.tv_pending_payments);

        // Statistik Cards
        cardTodayBookings = view.findViewById(R.id.card_today_bookings);
        cardPendingPayments = view.findViewById(R.id.card_pending_payments);
        cardRevenue = view.findViewById(R.id.card_revenue);

        // Tambahan jika di XML ada card_total_bookings (opsional, sesuaikan XML)
        // cardTotalBookings = view.findViewById(R.id.card_total_bookings);

        // REVISI PENTING: Casting ke Button untuk menghindari ClassCastException
        btnCreateBooking = view.findViewById(R.id.card_create_booking);

        // Init Tombol Logout
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void initDatabase() {
        if (getContext() != null) {
            dbHelper = new DatabaseHelper(getContext());
            sessionManager = new SessionManager(getContext());
        }
    }

    private void loadDashboardData() {
        if (sessionManager == null || dbHelper == null) return;

        String userName = sessionManager.getUserName();
        String role = sessionManager.isAdmin() ? "Administrator" : "Resepsionis";
        tvWelcomeAdmin.setText("Halo, " + userName + "\n(" + role + ")");

        // Load statistics dari Database
        int totalBookings = dbHelper.getTotalBookingsCount();
        int todayBookings = dbHelper.getTodayBookingsCount();
        double todayRevenue = dbHelper.getTodayRevenue();
        int pendingPayments = dbHelper.getPendingPaymentsCount();

        tvTotalBookings.setText(String.valueOf(totalBookings));
        tvTodayBookings.setText(String.valueOf(todayBookings));

        // Format Rupiah
        java.text.NumberFormat formatRupiah = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        tvTodayRevenue.setText(formatRupiah.format(todayRevenue));

        tvPendingPayments.setText(String.valueOf(pendingPayments));
    }

    private void setupClickListeners() {
        // Navigasi ke Fragment Lain
        cardTodayBookings.setOnClickListener(v -> navigateToBookings());
        cardPendingPayments.setOnClickListener(v -> navigateToPayments());

        // Tombol Buat Booking Baru
        btnCreateBooking.setOnClickListener(v -> openCreateBooking());

    }

    private void navigateToBookings() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_admin, new AdminBookingsFragment())
                .addToBackStack(null) // Tambahkan ke backstack agar bisa kembali
                .commit();
    }

    private void navigateToPayments() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_admin, new AdminPaymentsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void openCreateBooking() {
        // Pastikan Activity AdminCreateBookingActivity sudah dibuat
        try {
            Intent intent = new Intent(requireContext(), AdminCreateBookingActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Fitur belum tersedia", Toast.LENGTH_SHORT).show();
        }
    }


}