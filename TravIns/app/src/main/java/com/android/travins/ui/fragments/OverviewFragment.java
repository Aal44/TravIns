package com.android.travins.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.travins.R;
import com.android.travins.data.models.Places;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    private Places place;
    private TextView price;
    private TextView rate;
    private TextView review;
    private TextView location;
    private TextView desc;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public OverviewFragment(Places place){
        this.place = place;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        price = view.findViewById(R.id.tvPrice);
        rate = view.findViewById(R.id.tvRate);
        review = view.findViewById(R.id.tvReviews);
        desc = view.findViewById(R.id.tvDesc);
        location = view.findViewById(R.id.tvLocation);

        if (place!=null){
            price.setText(place.getPrice());
            rate.setText(place.getRate());
            review.setText(place.getTotal_review()+" reviews");
            desc.setText(place.getDescription());
            location.setText(place.getLocation());
        }
    }
}