package com.android.travins.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.data.models.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private ArrayList<Review> reviews = new ArrayList<>();
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void updateData(ArrayList<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review r = reviews.get(position);
        holder.text.setText(r.getText());
        holder.author.setText(r.getAuthor());
        holder.ratingBar.setRating(Float.parseFloat(r.getRate()));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView text;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.textAuthor);
            text = itemView.findViewById(R.id.txtReview);
            ratingBar = itemView.findViewById(R.id.rating);
        }
    }
}
