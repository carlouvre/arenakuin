package com.example.arenakuin.adapters;

import android.content.Context;
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

public class AdminPaymentAdapter extends RecyclerView.Adapter<AdminPaymentAdapter.PaymentViewHolder> {

    private Context context;
    private List<Booking> paymentList;
    private OnPaymentActionListener actionListener;

    public interface OnPaymentActionListener {
        void onConfirmPayment(Booking booking);
        void onRejectPayment(Booking booking);
    }

    public AdminPaymentAdapter(Context context, List<Booking> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    public void setOnPaymentActionListener(OnPaymentActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Booking booking = paymentList.get(position);

        holder.tvBookingId.setText("#" + booking.getBookingId());
        holder.tvCustomerName.setText(booking.getCustomerName());
        holder.tvVenueName.setText(booking.getVenueName());
        holder.tvBookingDate.setText(booking.getBookingDate());
        holder.tvBookingTime.setText(booking.getDuration());
        holder.tvTotalPrice.setText(booking.getFormattedPrice());
        holder.tvPaymentMethod.setText(booking.getPaymentMethod());

        // Click listeners
        holder.btnConfirmPayment.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onConfirmPayment(booking);
            }
        });

        holder.btnRejectPayment.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRejectPayment(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public void updateList(List<Booking> newList) {
        this.paymentList = newList;
        notifyDataSetChanged();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvCustomerName, tvVenueName;
        TextView tvBookingDate, tvBookingTime, tvTotalPrice, tvPaymentMethod;
        Button btnConfirmPayment, btnRejectPayment;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tv_booking_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvVenueName = itemView.findViewById(R.id.tv_venue_name);
            tvBookingDate = itemView.findViewById(R.id.tv_booking_date);
            tvBookingTime = itemView.findViewById(R.id.tv_booking_time);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            btnConfirmPayment = itemView.findViewById(R.id.btn_confirm_payment);
            btnRejectPayment = itemView.findViewById(R.id.btn_reject_payment);
        }
    }
}

