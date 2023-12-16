package com.android.travins.ui.adapters;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> implements Filterable {

    private Context context;
    private Integer id;
    private ArrayList<Places> places = new ArrayList<>();
    private ArrayList<Places> filteredPlaces = new ArrayList<>();

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Constructor to receive the context
    public HorizontalAdapter(Context context, Integer id) {
        this.context = context;
        this.id = id;
    }

    public void updateData(ArrayList<Places> places){
        this.places = places;
        this.filteredPlaces = new ArrayList<>(places);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind data or perform operations for each item
        Places p = filteredPlaces.get(position);

        if (id == 1){
            holder.btnFavPlace.setVisibility(View.GONE);
        }else{
            holder.btnFavPlace.setVisibility(View.VISIBLE);
            if (p.isFav()){
                holder.fav.setImageResource(R.drawable.ic_heart_red);
            }else{
                holder.fav.setImageResource(R.drawable.ic_heart);
            }
        }
        holder.name.setText(p.getName());
        holder.location.setText(p.getLocation());
        holder.rate.setText(p.getRate());
        Glide.with(context)
                .load(p.getImage())
                .into(holder.imgPlace);

        holder.itemView.setOnClickListener(v -> {
            clickListener.onItemClick(p);
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of your data list
        // For example: return dataList.size();
        return filteredPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView location;
        TextView rate;
        ImageView imgPlace;
        LinearLayout btnFavPlace;
        CardView cardView;
        ImageView fav;

        public ViewHolder(View itemView) {
            super(itemView);

            btnFavPlace = itemView.findViewById(R.id.btnFavPlace);
            name = itemView.findViewById(R.id.tvPlaceName);
            location = itemView.findViewById(R.id.tvLocation);
            rate = itemView.findViewById(R.id.tvRating);
            imgPlace = itemView.findViewById(R.id.ivPlaceImg);
            cardView = itemView.findViewById(R.id.cvPlace);
            fav = itemView.findViewById(R.id.fav);

            LinearLayout.LayoutParams layoutParams;
            if (id==1){
                layoutParams = new LinearLayout.LayoutParams(
                        dpToPx(context, 250),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            }else{
                layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            cardView.setLayoutParams(layoutParams);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<Places> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered.addAll(places);
                } else {
                    for (Places item : places) {
                        if (item.getName().toLowerCase().contains(query)) {
                            filtered.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPlaces.clear();
                filteredPlaces.addAll((List<Places>) results.values);
                notifyDataSetChanged();
            }
        };
    }
    public interface OnItemClickListener {
        void onItemClick(Places place);
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

