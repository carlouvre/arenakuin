package com.example.arenakuin.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arenakuin.R;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword, tvAdminInfo;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Inisialisasi Database & Session
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // 2. Cek apakah user sudah login sebelumnya?
        // Jika YA, langsung arahkan ke halaman yang sesuai dan tutup LoginActivity
        if (sessionManager.isLoggedIn()) {
            navigateByRole();
            return; // Hentikan eksekusi onCreate agar tidak lanjut ke initViews
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvAdminInfo = findViewById(R.id.tv_admin_info);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Hubungi admin untuk reset password", Toast.LENGTH_SHORT).show();
        });

        tvAdminInfo.setOnClickListener(v -> {
            showAdminLoginInfo();
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            Cursor cursor = dbHelper.loginUser(email, password);

            if (cursor != null && cursor.moveToFirst()) {
                // Ambil data dari database
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String userRole = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                // Debugging (Opsional, bisa dilihat di Logcat)
                Log.d("LOGIN_DEBUG", "User: " + userName + ", Role: " + userRole);

                // SIMPAN SESI
                sessionManager.createLoginSession(userId, userName, userEmail, userRole);

                // Feedback user
                String roleText = getRoleDisplayName(userRole);
                Toast.makeText(this, "Login berhasil sebagai " + roleText, Toast.LENGTH_SHORT).show();

                cursor.close();

                // Pindah halaman sesuai role
                navigateByRole();

            } else {
                Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method PENTING: Mengatur arah tujuan berdasarkan Role
    private void navigateByRole() {
        String role = sessionManager.getUserRole();
        Intent intent;

        // Gunakan equalsIgnoreCase agar "Admin", "admin", "ADMIN" dianggap sama
        if ("admin".equalsIgnoreCase(role) || "receptionist".equalsIgnoreCase(role)) {
            // Arahkan ke Dashboard Admin
            intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else {
            // Arahkan ke Dashboard Customer (Default)
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        // Clear Task flags: Agar user tidak bisa kembali ke halaman login dengan tombol Back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private String getRoleDisplayName(String role) {
        if (role == null) return "Pengguna";

        switch (role.toLowerCase()) {
            case "admin":
                return "Administrator";
            case "receptionist":
                return "Resepsionis";
            case "customer":
            default:
                return "Customer";
        }
    }

    private void showAdminLoginInfo() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Info Login Admin")
                .setMessage("Login Admin Default:\n\n" +
                        "Email: admin@arenakuin.com\n" +
                        "Password: admin123\n\n" +
                        "Login Resepsionis:\n" +
                        "Email: receptionist@arenakuin.com\n" +
                        "Password: resep123")
                .setPositiveButton("OK", null)
                .show();
    }
}