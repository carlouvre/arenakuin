package com.example.arenakuin.adapters;

import android.content.Context;
import android.graphics.Color; // Gunakan Color class bawaan Android
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.models.Booking;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.AdminBookingViewHolder> {

    private final Context context;
    private List<Booking> bookingList;
    private OnBookingActionListener actionListener;

    public interface OnBookingActionListener {
        void onUpdateStatus(Booking booking, String newStatus);
        void onViewDetails(Booking booking);
    }

    public AdminBookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public AdminBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new AdminBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set Data Text
        holder.tvBookingId.setText("#" + booking.getBookingId());
        holder.tvCustomerName.setText(booking.getCustomerName());
        holder.tvVenueName.setText(booking.getVenueName());

        // Cek null safety jika model belum punya method getVenueType()
        // holder.tvVenueType.setText(booking.getVenueType());

        holder.tvBookingDate.setText(booking.getBookingDate());

        // Gunakan getDuration() atau gabungan start-end time
        // holder.tvBookingTime.setText(booking.getStartTime() + " - " + booking.getEndTime());
        // Asumsi model punya method getDuration()
        holder.tvBookingTime.setText(booking.getStartTime() + " - " + booking.getEndTime());

        holder.tvTotalPrice.setText(booking.getFormattedPrice());

        // Status Badge
        holder.tvStatus.setText(booking.getStatus());
        holder.tvStatus.setTextColor(getStatusColor(booking.getStatus()));

        // Payment Status Badge
        holder.tvPaymentStatus.setText(booking.getPaymentStatus());
        holder.tvPaymentStatus.setTextColor(getPaymentStatusColor(booking.getPaymentStatus()));

        // Logic Tombol: Hanya muncul jika status 'Pending'
        if ("Pending".equalsIgnoreCase(booking.getStatus())) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        // Listener Tombol
        holder.btnConfirm.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onUpdateStatus(booking, "Confirmed");
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onUpdateStatus(booking, "Cancelled");
            }
        });

        holder.btnDetail.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewDetails(booking);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onViewDetails(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Helper Warna Status (Menggunakan kode Hex agar aman dari resource missing)
    private int getStatusColor(String status) {
        switch (status) {
            case "Confirmed": return Color.parseColor("#4CAF50"); // Hijau
            case "Pending": return Color.parseColor("#FF9800");   // Orange
            case "Cancelled": return Color.parseColor("#F44336"); // Merah
            case "Completed": return Color.parseColor("#2196F3"); // Biru
            default: return Color.GRAY;
        }
    }

    private int getPaymentStatusColor(String status) {
        switch (status) {
            case "Paid": return Color.parseColor("#4CAF50"); // Hijau
            case "Unpaid": return Color.parseColor("#F44336"); // Merah
            default: return Color.GRAY;
        }
    }

    public void updateList(List<Booking> newList) {
        this.bookingList = newList;
        notifyDataSetChanged();
    }

    // ViewHolder Class
    static class AdminBookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvCustomerName, tvVenueName; //, tvVenueType;
        TextView tvBookingDate, tvBookingTime, tvTotalPrice;
        TextView tvStatus, tvPaymentStatus;
        Button btnConfirm, btnReject, btnDetail;

        public AdminBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            // Binding ID sesuai dengan item_admin_booking.xml yang baru
            tvBookingId = itemView.findViewById(R.id.tv_booking_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            // tvVenueType = itemView.findViewById(R.id.tv_venue_type); // Jika di XML baru tidak ada, komen ini

            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);

            // Perhatikan ID ini, sesuaikan dengan XML (tv_time atau tv_booking_time)
            // Di XML revisi saya pakai tv_time
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);

            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);

            // TOMBOL (ID yang diperbaiki)
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnReject = itemView.findViewById(R.id.btn_reject); // Dulu btn_cancel
            btnDetail = itemView.findViewById(R.id.btn_detail); // Dulu btn_view_details
        }
    }
}