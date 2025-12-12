package com.example.arenakuin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.arenakuin.R;
import com.example.arenakuin.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin, tvInfoCustomer;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initDatabase();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        tvInfoCustomer = findViewById(R.id.tv_info_customer);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());

        tvLogin.setOnClickListener(v -> finish());

        // Info text
        tvInfoCustomer.setText("Register hanya untuk Customer. " +
                "Akun Admin/Resepsionis dibuat oleh administrator.");
    }

    private void performRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateInput(name, email, phone, password, confirmPassword)) {
            // Register as CUSTOMER only
            long result = dbHelper.registerCustomer(name, email, password, phone);

            if (result != -1) {
                Toast.makeText(this, "Registrasi berhasil! Silakan login",
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Email sudah terdaftar!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String name, String email, String phone,
                                  String password, String confirmPassword) {
        if (name.isEmpty()) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return false;
        }

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

        if (phone.isEmpty()) {
            etPhone.setError("Nomor telepon tidak boleh kosong");
            etPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10 || phone.length() > 13) {
            etPhone.setError("Nomor telepon tidak valid");
            etPhone.requestFocus();
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

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}