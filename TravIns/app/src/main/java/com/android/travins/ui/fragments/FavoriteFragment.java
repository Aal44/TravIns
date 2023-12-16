package com.android.travins.ui.fragments;

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
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.ui.activities.PlaceDetailActivity;
import com.android.travins.ui.adapters.HorizontalAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class FavoriteFragment extends Fragment implements HorizontalAdapter.OnItemClickListener{

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private HorizontalAdapter adapter;
    private ArrayList<Places> listDest = new ArrayList<>();

    private RecyclerView rvFav;
    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        rvFav = view.findViewById(R.id.rvFav);
        adapter = new HorizontalAdapter(this.getContext(),2);
        adapter.setOnItemClickListener(this);

        fetchData();
        rvFav.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        rvFav.setAdapter(adapter);
    }

    private void fetchData() {
        db.collection("users").document(auth.getCurrentUser().getUid()).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Places places = new Places(document.getId(),
                                    (String) data.get("name"), (String) data.get("image"),
                                    (String) data.get("description"), (String) data.get("price"),
                                    (String) data.get("location"), (String) data.get("rate"),
                                    (String) data.get("total_review"));
                            places.setFav(true);
                            listDest.add(places);
                        }
                        adapter.updateData(listDest);

                    } else {
                        Toast.makeText(getActivity(),"Fetch data failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onItemClick(Places place) {
        Intent intent = new Intent(this.getActivity(), PlaceDetailActivity.class);
        intent.putExtra("place",place);
        startActivity(intent);
    }
}