package com.example.arenakuin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arenakuin.R;
import com.example.arenakuin.models.Review;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.userName.setText(review.getUserName());
        holder.comment.setText(review.getComment());
        holder.rating.setRating(review.getRating());
        holder.reviewDate.setText(formatDate(review.getReviewDate()));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                return outputFormat.format(date);
            }
            return dateString;
        } catch (ParseException e) {
            return dateString;
        }
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userName, comment, reviewDate;
        RatingBar rating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.review_user_name);
            comment = itemView.findViewById(R.id.review_comment);
            rating = itemView.findViewById(R.id.review_rating);
            reviewDate = itemView.findViewById(R.id.review_date);
        }
    }
}
