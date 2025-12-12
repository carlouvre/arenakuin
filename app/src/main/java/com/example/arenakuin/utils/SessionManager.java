package com.example.arenakuin.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ArenaKuInSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole"; // NEW: customer, admin, receptionist

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String userName, String userEmail, String userRole) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_ROLE, userRole);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, "customer");
    }

    // Check if user is customer
    public boolean isCustomer() {
        return "customer".equals(getUserRole());
    }

    // Check if user is admin
    public boolean isAdmin() {
        return "admin".equals(getUserRole());
    }

    // Check if user is receptionist
    public boolean isReceptionist() {
        return "receptionist".equals(getUserRole());
    }

    // Check if user has admin or receptionist privileges
    public boolean hasAdminPrivileges() {
        return isAdmin() || isReceptionist();
    }

    public void logoutUser() {
        // Menghapus semua data dari Shared Preferences
        editor.clear();
        editor.commit();

        // Catatan: Redirect ke LoginActivity dilakukan di Fragment/Activity pemanggil
    }
}