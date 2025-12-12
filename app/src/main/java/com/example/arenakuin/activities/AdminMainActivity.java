package com.example.arenakuin.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.arenakuin.R;
import com.example.arenakuin.fragments.admin.*;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        bottomNav = findViewById(R.id.bottom_navigation_admin);

        // Load default fragment
        loadFragment(new AdminDashboardFragment());

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_admin_dashboard) {
                fragment = new AdminDashboardFragment();
            } else if (itemId == R.id.nav_admin_bookings) {
                fragment = new AdminBookingsFragment();
            } else if (itemId == R.id.nav_admin_venues) {
                fragment = new AdminVenuesFragment();
            } else if (itemId == R.id.nav_admin_payments) {
                fragment = new AdminPaymentsFragment();
            } else if (itemId == R.id.nav_admin_profile) {
                fragment = new AdminProfileFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_admin, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}