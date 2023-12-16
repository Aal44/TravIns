package com.android.travins.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.databinding.ActivityPlaceDetailBinding;
import com.android.travins.ui.adapters.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class PlaceDetailActivity extends AppCompatActivity {

    private ActivityPlaceDetailBinding binding;
    private boolean isFav;
    private ArrayList<String> tabsText = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Places places = (Places) getIntent().getSerializableExtra("place");
        if (places!=null){
            binding.tvNamePlace.setText(places.getName());
            binding.tvLocationPlace.setText(places.getLocation());
            binding.tvRate.setText(places.getRate());
            Glide.with(this)
                    .load(places.getImage())
                    .into(binding.ivDetailPlace);
            fetchFavData(places);
        }


        tabsText.add("Overview");
        tabsText.add("Review");

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),getLifecycle());
        adapter.setPlaces(places);

        binding.pager.setAdapter(adapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout,binding.pager,(tab, position) ->
                tab.setText(tabsText.get(position))
        );
        tabLayoutMediator.attach();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnFav.setOnClickListener(v -> {
            if (isFav){
                db.collection("users").document(auth.getCurrentUser().getUid()).collection("favorites").document(places.getId()).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Removed from favorite.", Toast.LENGTH_SHORT).show();
                        isFav = false;
                        binding.btnFav.setImageResource(R.drawable.ic_heart);
                    }
                });
            }else{
                db.collection("users").document(auth.getCurrentUser().getUid()).collection("favorites").document(places.getId()).set(places).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Added to favorite.", Toast.LENGTH_SHORT).show();
                        isFav = true;
                        binding.btnFav.setImageResource(R.drawable.ic_heart_red);
                    }
                });
            }
        });
    }

    private void fetchFavData(Places places) {
        db.collection("users").document(auth.getCurrentUser().getUid()).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            if (document.getId().equals(places.getId())){
                                isFav = true;
                                binding.btnFav.setImageResource(R.drawable.ic_heart_red);
                            }
                        }
                    } else {
                    }
                });
    }
}