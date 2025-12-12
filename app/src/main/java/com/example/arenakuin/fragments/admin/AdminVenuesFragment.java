package com.example.arenakuin.fragments.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arenakuin.R;
import com.example.arenakuin.adapters.AdminVenueAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Venue;

import java.util.ArrayList;
import java.util.List;

public class AdminVenuesFragment extends Fragment {

    private RecyclerView rvVenues;
    private View tvEmptyState; // Menggunakan View agar fleksibel (bisa LinearLayout/TextView)

    private AdminVenueAdapter venueAdapter;
    private DatabaseHelper dbHelper;

    private List<Venue> venueList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_venues, container, false);

        initViews(view);
        initDatabase();
        loadAllVenues();

        return view;
    }

    private void initViews(View view) {
        rvVenues = view.findViewById(R.id.rv_admin_venues);

        // --- PERBAIKAN PENTING DI SINI ---
        // Sesuaikan dengan ID di XML (layout_empty_state)
        tvEmptyState = view.findViewById(R.id.layout_empty_state);
    }

    private void initDatabase() {
        if (getContext() != null) {
            dbHelper = new DatabaseHelper(requireContext());
        }
    }

    private void loadAllVenues() {
        venueList = new ArrayList<>();

        if (dbHelper == null) return;

        // Load ALL venues (active and inactive)
        // Pastikan method getAllVenues() sudah ada di DatabaseHelper (dari revisi sebelumnya)
        Cursor cursor = dbHelper.getAllVenues();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Venue venue = createVenueFromCursor(cursor);
                    venueList.add(venue);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        displayVenues();
    }

    private void displayVenues() {
        // Cek null pointer safety
        if (rvVenues == null || tvEmptyState == null) return;

        if (venueList.isEmpty()) {
            rvVenues.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvVenues.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            venueAdapter = new AdminVenueAdapter(requireContext(), venueList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            rvVenues.setLayoutManager(layoutManager);
            rvVenues.setAdapter(venueAdapter);

            venueAdapter.setOnVenueActionListener(new AdminVenueAdapter.OnVenueActionListener() {
                @Override
                public void onToggleActive(Venue venue, boolean isActive) {
                    toggleVenueStatus(venue, isActive);
                }

                @Override
                public void onEditVenue(Venue venue) {
                    showVenueDetails(venue);
                }
            });
        }
    }

    private Venue createVenueFromCursor(Cursor cursor) {
        int venueId = cursor.getInt(cursor.getColumnIndexOrThrow("venue_id"));
        String venueName = cursor.getString(cursor.getColumnIndexOrThrow("venue_name"));
        String venueType = cursor.getString(cursor.getColumnIndexOrThrow("venue_type"));
        String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price_per_hour"));
        String facilities = cursor.getString(cursor.getColumnIndexOrThrow("facilities"));
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
        double rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating"));
        String openTime = cursor.getString(cursor.getColumnIndexOrThrow("open_time"));
        String closeTime = cursor.getString(cursor.getColumnIndexOrThrow("close_time"));
        int isActive = cursor.getInt(cursor.getColumnIndexOrThrow("is_active"));

        Venue venue = new Venue(venueId, venueName, venueType, location, price,
                facilities, imageUrl, rating, openTime, closeTime);
        venue.setActive(isActive == 1); // 1 = true, 0 = false

        return venue;
    }

    private void toggleVenueStatus(Venue venue, boolean isActive) {
        boolean success = dbHelper.toggleVenueStatus(venue.getVenueId(), isActive);
        if (success) {
            venue.setActive(isActive);
            String status = isActive ? "diaktifkan" : "dinonaktifkan";
            Toast.makeText(requireContext(),
                    "Venue berhasil " + status,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    "Gagal mengubah status venue",
                    Toast.LENGTH_SHORT).show();
            // Jika gagal, refresh list untuk mengembalikan posisi switch
            loadAllVenues();
        }
    }

    private void showVenueDetails(Venue venue) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(venue.getVenueName())
                .setMessage(
                        "Tipe: " + venue.getVenueType() + "\n" +
                                "Lokasi: " + venue.getLocation() + "\n" +
                                "Harga: " + venue.getFormattedPrice() + "\n" +
                                "Rating: " + venue.getRating() + "\n" +
                                "Jam: " + venue.getOperatingHours() + "\n" +
                                "Fasilitas: " + venue.getFacilities() + "\n" +
                                "Status: " + (venue.isActive() ? "Aktif" : "Nonaktif")
                )
                .setPositiveButton("OK", null)
                .show();
    }
}