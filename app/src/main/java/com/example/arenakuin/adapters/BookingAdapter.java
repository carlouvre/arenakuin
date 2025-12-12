package com.example.arenakuin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.models.Booking;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.venueName.setText(booking.getVenueName());
        holder.venueType.setText(booking.getVenueType());
        holder.bookingDate.setText(booking.getBookingDate());
        holder.bookingTime.setText(booking.getDuration());
        holder.totalPrice.setText(booking.getFormattedPrice());
        holder.status.setText(booking.getStatus());

        // Set status color
        int statusColor;
        switch (booking.getStatus()) {
            case "Confirmed":
                statusColor = ContextCompat.getColor(context, R.color.success);
                break;
            case "Pending":
                statusColor = ContextCompat.getColor(context, R.color.warning);
                break;
            case "Cancelled":
                statusColor = ContextCompat.getColor(context, R.color.error);
                break;
            default:
                statusColor = ContextCompat.getColor(context, R.color.text_secondary);
        }
        holder.status.setTextColor(statusColor);

        // Set payment method icon
        holder.paymentMethod.setText(booking.getPaymentMethod());

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (onBookingClickListener != null) {
                onBookingClickListener.onBookingClick(booking, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateList(List<Booking> newList) {
        this.bookingList = newList;
        notifyDataSetChanged();
    }

    // Booking Click Listener Interface
    private OnBookingClickListener onBookingClickListener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking, int position);
    }

    public void setOnBookingClickListener(OnBookingClickListener listener) {
        this.onBookingClickListener = listener;
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView venueName, venueType, bookingDate, bookingTime;
        TextView totalPrice, status, paymentMethod;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            venueName = itemView.findViewById(R.id.booking_venue_name);
            venueType = itemView.findViewById(R.id.booking_venue_type);
            bookingDate = itemView.findViewById(R.id.booking_date);
            bookingTime = itemView.findViewById(R.id.booking_time);
            totalPrice = itemView.findViewById(R.id.booking_total_price);
            status = itemView.findViewById(R.id.booking_status);
            paymentMethod = itemView.findViewById(R.id.booking_payment_method);
        }
    }
}