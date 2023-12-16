package com.android.travins.ui.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.data.models.Review;
import com.android.travins.ui.activities.LoginActivity;
import com.android.travins.ui.activities.RegisterActivity;
import com.android.travins.ui.adapters.HorizontalAdapter;
import com.android.travins.ui.adapters.ReviewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReviewFragment extends Fragment {

    private Places place;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ArrayList<Review> listReview = new ArrayList<>();

    private RecyclerView rvListReview;
    private ReviewAdapter adapter;

    private TextView loadMore;
    private RatingBar ratingbar;
    private Button btnSubmit;
    private EditText editText;


    public ReviewFragment() {
        // Required empty public constructor
    }

    public ReviewFragment(Places place){
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
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new ReviewAdapter(this.getContext());
        rvListReview = view.findViewById(R.id.rvListReview);
        loadMore = view.findViewById(R.id.loadMore);
        ratingbar= view.findViewById(R.id.ratingBar);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        editText = view.findViewById(R.id.edtTxtReview);

        fetchData();

//        InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        rvListReview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        rvListReview.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> {
            String rate=String.valueOf(ratingbar.getRating());
            String text = editText.getText().toString();

            Map<String, Object> review = new HashMap<>();
            review.put("author", auth.getCurrentUser().getEmail());
            review.put("rate", rate);
            review.put("text", text);

            db.collection("places").document(place.getId()).collection("reviews").add(review)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(view.getContext(), "Review Submitted", Toast.LENGTH_SHORT).show();
                        fetchData();
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Error submitting review", Toast.LENGTH_SHORT).show();

                        }
                    });

        });


    }

    private void fetchData() {
        listReview = new ArrayList<>();
        db.collection("places").document(place.getId()).collection("reviews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Review review = new Review((String) data.get("author"), (String) data.get("text"), (String) data.get("rate"));

                            listReview.add(review);
                        }
                        adapter.updateData(listReview);
                        if (listReview.isEmpty()){
                            loadMore.setText("No Review");
                            loadMore.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(getActivity(),"Fetch data failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}