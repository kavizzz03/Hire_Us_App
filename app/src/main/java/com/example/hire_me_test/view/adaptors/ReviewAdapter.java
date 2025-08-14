package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hire_me_test.R;
import com.example.hire_me_test.model.model.data.Review;


import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    Context context;
    List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ratedBy,  jobDetails, feedback, createdAt;
        RatingBar rating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratedBy = itemView.findViewById(R.id.ratedBy);
            rating = itemView.findViewById(R.id.ratingBar);
            jobDetails = itemView.findViewById(R.id.jobDetails);
            feedback = itemView.findViewById(R.id.feedback);
            createdAt = itemView.findViewById(R.id.createdAt);
        }
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.ratedBy.setText("Rated by: " + review.getRatedBy());
        holder.rating.setRating(review.getRating());
        holder.jobDetails.setText(review.getJobTitle() + " at " + review.getCompanyName() + " (" + review.getDuration() + ")");
        holder.feedback.setText("Feedback: " + review.getFeedback());
        holder.createdAt.setText("Reviewed on: " + review.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
