package com.example.arenakuin.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class ExploreFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvVenues;

    private VenueAdapter venueAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<Venue> allVenues, filteredVenues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        initViews(view);
        initDatabase();
        loadAllVenues();
        setupSearch();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        rvVenues = view.findViewById(R.id.rv_explore_venues);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    private void loadAllVenues() {
        allVenues = new ArrayList<>();
        filteredVenues = new ArrayList<>();

        Cursor cursor = dbHelper.getAllVenues();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Venue venue = createVenueFromCursor(cursor);
                allVenues.add(venue);
                filteredVenues.add(venue);
            } while (cursor.moveToNext());
            cursor.close();
        }

        venueAdapter = new VenueAdapter(requireContext(), filteredVenues);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvVenues.setLayoutManager(layoutManager);
        rvVenues.setAdapter(venueAdapter);

        venueAdapter.setOnFavoriteClickListener((venue, position) -> {
            toggleFavorite(venue, position);
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVenues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterVenues(String query) {
        filteredVenues.clear();

        if (query.isEmpty()) {
            filteredVenues.addAll(allVenues);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Venue venue : allVenues) {
                if (venue.getVenueName().toLowerCase().contains(lowerQuery) ||
                        venue.getVenueType().toLowerCase().contains(lowerQuery) ||
                        venue.getLocation().toLowerCase().contains(lowerQuery)) {
                    filteredVenues.add(venue);
                }
            }
        }

        venueAdapter.updateList(filteredVenues);
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

        int userId = sessionManager.getUserId();
        boolean isFavorite = dbHelper.isFavorite(userId, venueId);
        venue.setFavorite(isFavorite);

        return venue;
    }

    private void toggleFavorite(Venue venue, int position) {
        int userId = sessionManager.getUserId();

        if (venue.isFavorite()) {
            dbHelper.removeFromFavorites(userId, venue.getVenueId());
            venue.setFavorite(false);
        } else {
            dbHelper.addToFavorites(userId, venue.getVenueId());
            venue.setFavorite(true);
        }

        venueAdapter.notifyItemChanged(position);
    }
}