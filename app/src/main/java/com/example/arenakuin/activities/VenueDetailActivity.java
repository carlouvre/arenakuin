package com.example.arenakuin.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri; // Tambahan untuk Maps
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout; // Tambahan untuk tombol Maps
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arenakuin.R;
import com.example.arenakuin.adapters.ReviewAdapter;
import com.example.arenakuin.database.DatabaseHelper;
import com.example.arenakuin.models.Review;
import com.example.arenakuin.models.Venue;
import com.example.arenakuin.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VenueDetailActivity extends AppCompatActivity {

    // UI Components
    private ImageView ivVenue;
    private ImageButton btnFavorite;
    private TextView tvVenueName, tvVenueType, tvLocation, tvPrice;
    private TextView tvOperatingHours, tvFacilities, tvRatingCount, tvNoReviews;
    private RatingBar rbVenue;
    private RecyclerView rvReviews;
    private Button btnBookNow;
    private MaterialToolbar toolbar;

    // Tambahan: Tombol Buka Maps
    private LinearLayout btnOpenMap;

    // Data & Tools
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ReviewAdapter reviewAdapter;
    private int venueId = -1;
    private Venue currentVenue;
    private List<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);

        initViews();
        initDatabase();

        // Ambil data intent
        venueId = getIntent().getIntExtra("venue_id", -1);

        // Validasi ID sebelum memuat data
        if (venueId != -1) {
            loadVenueDetails();
            loadReviews();
            setupListeners();
        } else {
            Toast.makeText(this, "Data venue tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ivVenue = findViewById(R.id.iv_venue);

        // Inisialisasi Toolbar
        toolbar = findViewById(R.id.toolbar);

        // Inisialisasi Tombol Favorite (cek tipe view agar aman)
        View favView = findViewById(R.id.btn_favorite);
        if (favView instanceof ImageButton) {
            btnFavorite = (ImageButton) favView;
        }

        tvVenueName = findViewById(R.id.tv_venue_name);
        tvVenueType = findViewById(R.id.tv_venue_type);
        tvLocation = findViewById(R.id.tv_location);

        // Inisialisasi Tombol Maps (Sesuai ID di XML baru)
        btnOpenMap = findViewById(R.id.btn_open_map);

        tvPrice = findViewById(R.id.tv_price);
        tvOperatingHours = findViewById(R.id.tv_operating_hours);
        tvFacilities = findViewById(R.id.tv_facilities);
        tvRatingCount = findViewById(R.id.tv_rating_count);
        rbVenue = findViewById(R.id.rb_venue);
        rvReviews = findViewById(R.id.rv_reviews);
        btnBookNow = findViewById(R.id.btn_book_now);
        tvNoReviews = findViewById(R.id.tv_no_reviews);
    }

    private void initDatabase() {
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
    }

    private void loadVenueDetails() {
        Cursor cursor = dbHelper.getVenueById(venueId);

        if (cursor != null && cursor.moveToFirst()) {
            currentVenue = createVenueFromCursor(cursor);
            cursor.close();
            displayVenueInfo();
        } else {
            Toast.makeText(this, "Venue tidak ditemukan di database", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private Venue createVenueFromCursor(Cursor cursor) {
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

        if (sessionManager.isLoggedIn()) {
            int userId = sessionManager.getUserId();
            boolean isFavorite = dbHelper.isFavorite(userId, venueId);
            venue.setFavorite(isFavorite);
        }

        return venue;
    }

    private void displayVenueInfo() {
        if (currentVenue == null) return;

        tvVenueName.setText(currentVenue.getVenueName());
        tvVenueType.setText(currentVenue.getVenueType());
        tvLocation.setText(currentVenue.getLocation());

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvPrice.setText(String.format("%s/jam", formatRupiah.format(currentVenue.getPricePerHour())));

        tvOperatingHours.setText(String.format("%s - %s", currentVenue.getOpenTime(), currentVenue.getCloseTime()));
        tvFacilities.setText(currentVenue.getFacilities());
        rbVenue.setRating((float) currentVenue.getRating());

        ivVenue.setImageResource(getImageResource(currentVenue.getImageUrl()));
        updateFavoriteButton();
    }

    private void loadReviews() {
        reviewList = new ArrayList<>();
        Cursor cursor = dbHelper.getVenueReviews(venueId);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int reviewId = cursor.getInt(cursor.getColumnIndexOrThrow("review_id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
                    String comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"));
                    String reviewDate = cursor.getString(cursor.getColumnIndexOrThrow("review_date"));

                    reviewList.add(new Review(reviewId, userId, userName, venueId,
                            rating, comment, reviewDate));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        tvRatingCount.setText(String.format(new Locale("id", "ID"), "(%d ulasan)", reviewList.size()));

        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        if (tvNoReviews != null) {
            if (reviewList.isEmpty()) {
                tvNoReviews.setVisibility(View.VISIBLE);
            } else {
                tvNoReviews.setVisibility(View.GONE);
            }
        }
    }

    private void setupListeners() {
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // --- LOGIKA TOMBOL BUKA MAPS (BARU) ---
        if (btnOpenMap != null) {
            btnOpenMap.setOnClickListener(v -> {
                if (currentVenue != null) {
                    openGoogleMaps(currentVenue.getVenueName(), currentVenue.getLocation());
                }
            });
        }

        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                if (!sessionManager.isLoggedIn()) {
                    Toast.makeText(this, "Silakan login untuk menyimpan favorit", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentVenue != null) toggleFavorite();
            });
        }

        btnBookNow.setOnClickListener(v -> {
            if (currentVenue == null) return;

            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Silakan login untuk melakukan booking", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(VenueDetailActivity.this, BookingActivity.class);
            intent.putExtra("venue_id", venueId);
            intent.putExtra("venue_name", currentVenue.getVenueName());
            intent.putExtra("price_per_hour", currentVenue.getPricePerHour());
            intent.putExtra("open_time", currentVenue.getOpenTime());
            intent.putExtra("close_time", currentVenue.getCloseTime());
            startActivity(intent);
        });
    }

    // --- Method Helper Membuka Google Maps ---
    private void openGoogleMaps(String venueName, String location) {
        // Query gabungan nama + lokasi agar akurat
        String mapQuery = venueName + " " + location;
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(mapQuery));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Prioritaskan aplikasi Maps

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // Jika aplikasi maps tidak ada, buka via browser
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(mapQuery));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void toggleFavorite() {
        int userId = sessionManager.getUserId();

        if (currentVenue.isFavorite()) {
            dbHelper.removeFromFavorites(userId, venueId);
            currentVenue.setFavorite(false);
            Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addToFavorites(userId, venueId);
            currentVenue.setFavorite(true);
            Toast.makeText(this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
        }
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        if (currentVenue != null && btnFavorite != null) {
            if (currentVenue.isFavorite()) {
                btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            }
        }
    }

    private int getImageResource(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return R.drawable.img_placeholder;
        }

        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (resourceId != 0) {
            return resourceId;
        } else {
            return R.drawable.img_placeholder;
        }
    }
}