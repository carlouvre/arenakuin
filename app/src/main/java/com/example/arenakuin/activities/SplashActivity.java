package com.example.arenakuin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.arenakuin.R;
import com.example.arenakuin.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                // --- PERBAIKAN LOGIKA: CEK ROLE DULU ---
                String role = sessionManager.getUserRole();

                if ("admin".equalsIgnoreCase(role) || "receptionist".equalsIgnoreCase(role)) {
                    // Jika Admin -> Masuk Dashboard Admin
                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                } else {
                    // Jika Customer -> Masuk Home Customer
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            } else {
                // Belum login -> Masuk Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}