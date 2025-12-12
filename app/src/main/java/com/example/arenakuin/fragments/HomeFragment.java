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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.adapters.CategoryAdapter;
import com.example.arenakuin.adapters.VenueAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Venue;
import com.example.arenakuin.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvWelcome;
    private RecyclerView rvCategories, rvPopularVenues, rvNearbyVenues;

    private CategoryAdapter categoryAdapter;
    private VenueAdapter popularAdapter, nearbyAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<CategoryAdapter.Category> categoryList;
    private List<Venue> popularVenues, nearbyVenues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        initDatabase();
        loadUserData();
        setupCategories();
        loadPopularVenues();
        loadNearbyVenues();

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvPopularVenues = view.findViewById(R.id.rv_popular_venues);
        rvNearbyVenues = view.findViewById(R.id.rv_nearby_venues);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    private void loadUserData() {
        String userName = sessionManager.getUserName();
        tvWelcome.setText("Hi, " + userName + "!");
    }

    private void setupCategories() {
        categoryList = new ArrayList<>();
        categoryList.add(new CategoryAdapter.Category("Basket", R.drawable.ic_basket));
        categoryList.add(new CategoryAdapter.Category("Futsal", R.drawable.ic_futsal));
        categoryList.add(new CategoryAdapter.Category("Padel", R.drawable.ic_padel));
        categoryList.add(new CategoryAdapter.Category("Badminton", R.drawable.ic_badminton));
        categoryList.add(new CategoryAdapter.Category("Voli", R.drawable.ic_voli));
        categoryList.add(new CategoryAdapter.Category("Gym & Yoga", R.drawable.ic_gym));

        categoryAdapter = new CategoryAdapter(requireContext(), categoryList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
        );
        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener((category, position) -> {
            loadVenuesByCategory(category.getName());
        });
    }

    private void loadPopularVenues() {
        popularVenues = new ArrayList<>();

        Cursor cursor = dbHelper.getAllVenues();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Venue venue = createVenueFromCursor(cursor);
                popularVenues.add(venue);
            } while (cursor.moveToNext() && popularVenues.size() < 5);
            cursor.close();
        }

        popularAdapter = new VenueAdapter(requireContext(), popularVenues);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
        );
        rvPopularVenues.setLayoutManager(layoutManager);
        rvPopularVenues.setAdapter(popularAdapter);

        setupFavoriteListener(popularAdapter);
    }

    private void loadNearbyVenues() {
        nearbyVenues = new ArrayList<>();

        Cursor cursor = dbHelper.getAllVenues();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Venue venue = createVenueFromCursor(cursor);
                nearbyVenues.add(venue);
            } while (cursor.moveToNext());
            cursor.close();
        }

        nearbyAdapter = new VenueAdapter(requireContext(), nearbyVenues);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvNearbyVenues.setLayoutManager(layoutManager);
        rvNearbyVenues.setAdapter(nearbyAdapter);

        setupFavoriteListener(nearbyAdapter);
    }

    private void loadVenuesByCategory(String category) {
        List<Venue> filteredVenues = new ArrayList<>();

        Cursor cursor = dbHelper.getVenuesByType(category);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Venue venue = createVenueFromCursor(cursor);
                filteredVenues.add(venue);
            } while (cursor.moveToNext());
            cursor.close();
        }

        nearbyAdapter.updateList(filteredVenues);

        if (filteredVenues.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Belum ada venue untuk kategori " + category,
                    Toast.LENGTH_SHORT).show();
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

        Venue venue = new Venue(venueId, venueName, venueType, location, price,
                facilities, imageUrl, rating, openTime, closeTime);

        // Check if venue is in favorites
        int userId = sessionManager.getUserId();
        boolean isFavorite = dbHelper.isFavorite(userId, venueId);
        venue.setFavorite(isFavorite);

        return venue;
    }

    private void setupFavoriteListener(VenueAdapter adapter) {
        adapter.setOnFavoriteClickListener((venue, position) -> {
            int userId = sessionManager.getUserId();

            if (venue.isFavorite()) {
                dbHelper.removeFromFavorites(userId, venue.getVenueId());
                venue.setFavorite(false);
                Toast.makeText(requireContext(),
                        "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addToFavorites(userId, venue.getVenueId());
                venue.setFavorite(true);
                Toast.makeText(requireContext(),
                        "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
            }

            adapter.notifyItemChanged(position);
        });
    }
}