package com.example.arenakuin.fragments.admin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.arenakuin.R;
import com.example.arenakuin.activities.LoginActivity;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

public class AdminProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserRole, tvTotalBookings;
    private CardView cardStatistics, cardSettings, cardAbout;
    private Button btnLogout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        initViews(view);
        initDatabase();
        loadUserProfile();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserRole = view.findViewById(R.id.tv_user_role);
        tvTotalBookings = view.findViewById(R.id.tv_total_bookings);
        cardStatistics = view.findViewById(R.id.card_statistics);
        cardSettings = view.findViewById(R.id.card_settings);
        cardAbout = view.findViewById(R.id.card_about);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    private void loadUserProfile() {
        int userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();

        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);

        // Format role display
        String roleDisplay = "";
        if ("admin".equals(userRole)) {
            roleDisplay = "Administrator";
        } else if ("receptionist".equals(userRole)) {
            roleDisplay = "Resepsionis";
        }
        tvUserRole.setText(roleDisplay);

        // Load statistics
        int totalBookings = dbHelper.getTotalBookingsCount();
        tvTotalBookings.setText(String.valueOf(totalBookings) + " Total Booking");
    }

    private void setupListeners() {
        cardStatistics.setOnClickListener(v -> {
            // Navigate to dashboard
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_admin, new AdminDashboardFragment())
                    .commit();
        });

        cardSettings.setOnClickListener(v -> {
            showSettingsDialog();
        });

        cardAbout.setOnClickListener(v -> {
            showAboutDialog();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Pengaturan")
                .setMessage("Fitur pengaturan akan segera tersedia")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Tentang ArenaKu.in")
                .setMessage(
                        "ArenaKu.in\n" +
                                "Version 2.0\n\n" +
                                "Sistem Reservasi Lapangan Olahraga\n\n" +
                                "© 2025 ArenaKu.in\n" +
                                "All rights reserved."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}