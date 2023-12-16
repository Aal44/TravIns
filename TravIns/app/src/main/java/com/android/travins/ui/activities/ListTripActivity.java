package com.android.travins.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.databinding.ActivityListTripBinding;
import com.android.travins.ui.adapters.HorizontalAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ListTripActivity extends AppCompatActivity implements HorizontalAdapter.OnItemClickListener {

    private ActivityListTripBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private HorizontalAdapter adapter;
    private ArrayList<Places> listDest = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listDest = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new HorizontalAdapter(this,2);
        adapter.setOnItemClickListener(this);
        fetchData();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.rvListInspiration.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvListInspiration.setAdapter(adapter);

        binding.edtTxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void fetchData() {
        db.collection("places")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Places places = new Places(document.getId(),
                                        (String) data.get("name"), (String) data.get("image"),
                                        (String) data.get("description"), (String) data.get("price"),
                                        (String) data.get("location"), (String) data.get("rate"),
                                        (String) data.get("total_review"));

                                fetchFavData(places);
                            }

                        } else {
                            Toast.makeText(ListTripActivity.this,"Fetch data failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private Places fetchFavData(Places places) {
        db.collection("users").document(auth.getCurrentUser().getUid()).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            if (document.getId().equals(places.getId())){
                                places.setFav(true);
                            }
                            listDest.add(places);
                        }
                        adapter.updateData(listDest);
                    } else {
                    }
                });

        return places;
    }

    @Override
    public void onItemClick(Places place) {
        Intent intent = new Intent(ListTripActivity.this, PlaceDetailActivity.class);
        intent.putExtra("place",place);
        startActivity(intent);
    }
}