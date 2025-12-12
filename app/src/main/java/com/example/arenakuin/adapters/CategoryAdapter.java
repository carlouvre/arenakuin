package com.example.arenakuin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // Added 'final' per warning suggestion
    private final Context context;
    private final List<Category> categoryList;
    private int selectedPosition = -1;

    public static class Category {
        // Added 'final' per warning suggestion
        private final String name;
        private final int iconResId;

        public Category(String name, int iconResId) {
            this.name = name;
            this.iconResId = iconResId;
        }

        public String getName() { return name; }
        public int getIconResId() { return iconResId; }
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Warning: Jangan gunakan variabel 'position' ini di dalam Listener!
        // Gunakan hanya untuk set data awal.
        Category category = categoryList.get(position);

        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResId());

        // Highlight selected category
        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(
                    context.getResources().getColor(R.color.primary)
            );
            holder.categoryName.setTextColor(
                    context.getResources().getColor(R.color.white)
            );
        } else {
            holder.cardView.setCardBackgroundColor(
                    context.getResources().getColor(R.color.white)
            );
            holder.categoryName.setTextColor(
                    context.getResources().getColor(R.color.text_primary)
            );
        }

        holder.itemView.setOnClickListener(v -> {
            // PERBAIKAN UTAMA DI SINI:
            // Ambil posisi real-time saat klik terjadi
            int currentPosition = holder.getAdapterPosition();

            // Cek keamanan jika item sudah tidak ada (misal sedang animasi delete)
            if (currentPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            selectedPosition = currentPosition;

            // Update UI untuk item sebelumnya dan yang baru dipilih
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (onCategoryClickListener != null) {
                // Ambil data category berdasarkan posisi terkini agar akurat
                onCategoryClickListener.onCategoryClick(categoryList.get(currentPosition), currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Category Click Listener Interface
    private OnCategoryClickListener onCategoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    // Changed to 'public' to fix visibility warning
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView categoryIcon;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.category_card);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}