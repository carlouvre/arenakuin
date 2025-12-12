package com.example.arenakuin.fragments;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arenakuin.R;
import com.example.arenakuin.adapters.VenueAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Venue;
import com.example.arenakuin.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvEmptyState;

    private VenueAdapter venueAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<Venue> favoriteVenues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViews(view);
        initDatabase();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load data setiap kali fragment muncul kembali (penting agar data selalu fresh)
        loadFavorites();
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void initDatabase() {
        if (getContext() != null) {
            dbHelper = new DatabaseHelper(getContext());
            sessionManager = new SessionManager(getContext());
        }
    }

    private void loadFavorites() {
        favoriteVenues = new ArrayList<>();
        int userId = sessionManager.getUserId();

        // Pastikan method getUserFavorites sudah ada di DatabaseHelper Anda
        Cursor cursor = dbHelper.getUserFavorites(userId);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Venue venue = createVenueFromCursor(cursor);
                    // Karena ini halaman favorit, set status favorite ke true agar iconnya terisi penuh
                    venue.setFavorite(true);
                    favoriteVenues.add(venue);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        updateUI();
    }

    private void updateUI() {
        if (favoriteVenues.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            if (venueAdapter == null) {
                venueAdapter = new VenueAdapter(requireContext(), favoriteVenues);
                // Menggunakan Grid Layout 2 kolom agar tampilan lebih rapi
                GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
                rvFavorites.setLayoutManager(layoutManager);
                rvFavorites.setAdapter(venueAdapter);

                // Listener menggunakan Method Reference agar lebih rapi (menghilangkan warning kuning)
                venueAdapter.setOnFavoriteClickListener(this::removeFavorite);
            } else {
                // Jika adapter sudah ada, cukup update datanya
                venueAdapter.updateList(favoriteVenues);
            }
        }
    }

    private Venue createVenueFromCursor(Cursor cursor) {
        // Menggunakan getColumnIndexOrThrow untuk keamanan jika nama kolom typo
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

        return new Venue(venueId, venueName, venueType, location, price,
                facilities, imageUrl, rating, openTime, closeTime);
    }

    // Method untuk menghapus favorit saat tombol love diklik di halaman ini
    private void removeFavorite(Venue venue, int position) {
        int userId = sessionManager.getUserId();

        // Hapus dari database
        dbHelper.removeFromFavorites(userId, venue.getVenueId());

        // Hapus dari list tampilan dan notifikasi adapter
        if (position >= 0 && position < favoriteVenues.size()) {
            favoriteVenues.remove(position);
            venueAdapter.notifyItemRemoved(position);

            // Cek lagi apakah list jadi kosong setelah dihapus
            if (favoriteVenues.isEmpty()) {
                rvFavorites.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        }

        Toast.makeText(requireContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
    }
}