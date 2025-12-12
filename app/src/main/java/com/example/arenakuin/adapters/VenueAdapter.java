package com.example.arenakuin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arenakuin.R;
import com.example.arenakuin.activities.VenueDetailActivity;
import com.example.arenakuin.models.Venue;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VenueViewHolder> {

    private Context context;
    private List<Venue> venueList;
    private OnFavoriteClickListener onFavoriteClickListener;

    public VenueAdapter(Context context, List<Venue> venueList) {
        this.context = context;
        this.venueList = venueList;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venueList.get(position);

        holder.venueName.setText(venue.getVenueName());
        holder.venueLocation.setText(venue.getLocation());

        // PERBAIKAN 1: Format Rupiah agar rapi (Rp120.000,00)
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.venuePrice.setText(formatRupiah.format(venue.getPricePerHour()));

        holder.venueRating.setRating((float) venue.getRating());
        holder.venueType.setText(venue.getVenueType());

        // PERBAIKAN 2: Load gambar dinamis sesuai nama di database
        int imageResource = getImageResource(venue.getImageUrl());
        holder.venueImage.setImageResource(imageResource);

        // Set favorite icon
        holder.favoriteIcon.setImageResource(
                venue.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );

        // Click listener ke Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VenueDetailActivity.class);
            intent.putExtra("venue_id", venue.getVenueId());
            context.startActivity(intent);
        });

        // Favorite click listener
        holder.favoriteIcon.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteClick(venue, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    // --- METHOD PENTING: MENCARI GAMBAR SECARA OTOMATIS ---
    private int getImageResource(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return R.drawable.img_placeholder;
        }

        // Mencari ID gambar di folder drawable berdasarkan nama string-nya
        int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        // Jika ditemukan, kembalikan ID-nya. Jika tidak (0), pakai placeholder.
        return resourceId != 0 ? resourceId : R.drawable.img_placeholder;
    }

    public void updateList(List<Venue> newList) {
        this.venueList = newList;
        notifyDataSetChanged();
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Venue venue, int position);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    static class VenueViewHolder extends RecyclerView.ViewHolder {
        ImageView venueImage, favoriteIcon;
        TextView venueName, venueLocation, venuePrice, venueType;
        RatingBar venueRating;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            venueImage = itemView.findViewById(R.id.venue_image);
            venueName = itemView.findViewById(R.id.venue_name);
            venueLocation = itemView.findViewById(R.id.venue_location);
            venuePrice = itemView.findViewById(R.id.venue_price);
            venueRating = itemView.findViewById(R.id.venue_rating);
            venueType = itemView.findViewById(R.id.venue_type);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }
}