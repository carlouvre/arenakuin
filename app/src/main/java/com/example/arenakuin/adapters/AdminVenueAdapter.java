package com.example.arenakuin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat; // Gunakan SwitchCompat agar lebih stabil
import androidx.recyclerview.widget.RecyclerView;

import com.arenakuin.R;
import com.example.arenakuin.models.Venue;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminVenueAdapter extends RecyclerView.Adapter<AdminVenueAdapter.VenueViewHolder> {

    private Context context;
    private List<Venue> venueList;
    private OnVenueActionListener actionListener;

    // Interface untuk aksi klik
    public interface OnVenueActionListener {
        void onToggleActive(Venue venue, boolean isActive);
        void onEditVenue(Venue venue);
    }

    public AdminVenueAdapter(Context context, List<Venue> venueList) {
        this.context = context;
        this.venueList = venueList;
    }

    public void setOnVenueActionListener(OnVenueActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_venue, parent, false);
        return new VenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VenueViewHolder holder, int position) {
        Venue venue = venueList.get(position);

        holder.tvVenueName.setText(venue.getVenueName());
        holder.tvVenueType.setText(venue.getVenueType());
        holder.tvLocation.setText(venue.getLocation());

        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvPrice.setText(formatRupiah.format(venue.getPricePerHour()));

        // --- PERBAIKAN: Load Gambar Aman ---
        if (holder.ivVenue != null) {
            int imageResId = getImageResource(venue.getImageUrl());
            holder.ivVenue.setImageResource(imageResId);
        }

        // --- PERBAIKAN: Hindari Bug Switch saat Scroll ---
        holder.switchActive.setOnCheckedChangeListener(null); // Lepas listener dulu
        holder.switchActive.setChecked(venue.isActive());   // Set status

        // Pasang listener baru
        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (actionListener != null && buttonView.isPressed()) { // Cek isPressed agar tidak terpanggil otomatis
                actionListener.onToggleActive(venue, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditVenue(venue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    public void updateList(List<Venue> newList) {
        this.venueList = newList;
        notifyDataSetChanged();
    }

    // --- METHOD HELPER: LOAD GAMBAR DINAMIS ---
    private int getImageResource(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return R.drawable.img_placeholder;
        }
        int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        return resourceId != 0 ? resourceId : R.drawable.img_placeholder;
    }

    static class VenueViewHolder extends RecyclerView.ViewHolder {
        TextView tvVenueName, tvVenueType, tvLocation, tvPrice;
        SwitchCompat switchActive; // Ganti ke SwitchCompat
        ImageView ivVenue; // Tambahan untuk gambar (jika ada di layout XML)

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            tvVenueType = itemView.findViewById(R.id.tv_venue_type);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
            switchActive = itemView.findViewById(R.id.switch_active);

            // Cek apakah di layout item_admin_venue.xml ada ImageView?
            // Jika Anda belum menambahkannya di XML, ini akan null (aman karena ada pengecekan if != null di atas)
            // Disarankan menambahkan ImageView di XML agar admin bisa lihat foto venue.
            try {
                ivVenue = itemView.findViewById(R.id.venue_image); // Sesuaikan ID dengan XML Anda
            } catch (Exception e) {
                ivVenue = null;
            }
        }
    }
}