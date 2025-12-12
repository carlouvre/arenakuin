package com.example.arenakuin.fragments;

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
import androidx.fragment.app.Fragment;
import com.arenakuin.R;
import com.example.arenakuin.activities.LoginActivity;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private TextView tvMembershipType, tvPoints, tvTotalBookings;
    private Button btnEditProfile, btnLogout;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        initDatabase();
        loadUserProfile();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserPhone = view.findViewById(R.id.tv_user_phone);
        tvMembershipType = view.findViewById(R.id.tv_membership_type);
        tvPoints = view.findViewById(R.id.tv_points);
        tvTotalBookings = view.findViewById(R.id.tv_total_bookings);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    private void loadUserProfile() {
        int userId = sessionManager.getUserId();

        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            int points = cursor.getInt(cursor.getColumnIndexOrThrow("points"));
            String membership = cursor.getString(cursor.getColumnIndexOrThrow("membership_type"));

            tvUserName.setText(name);
            tvUserEmail.setText(email);
            tvUserPhone.setText(phone);
            tvPoints.setText(String.valueOf(points) + " poin");
            tvMembershipType.setText(membership);

            cursor.close();
        }

        // Load total bookings
        Cursor bookingCursor = dbHelper.getUserBookings(userId);
        if (bookingCursor != null) {
            int totalBookings = bookingCursor.getCount();
            tvTotalBookings.setText(String.valueOf(totalBookings) + " booking");
            bookingCursor.close();
        }
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            android.widget.Toast.makeText(requireContext(),
                    "Fitur akan segera tersedia", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
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