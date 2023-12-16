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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.data.models.Places;
import com.android.travins.ui.activities.ListTripActivity;
import com.android.travins.ui.activities.PlaceDetailActivity;
import com.android.travins.ui.adapters.HorizontalAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment implements HorizontalAdapter.OnItemClickListener {

    private ArrayList<Places> listDest = new ArrayList<>();
    private RecyclerView rvInspiration;
    private RecyclerView rvFreeDest;
    private Button btnIns;
    private TextView seeAllIns;
    private FirebaseFirestore db;

    private HorizontalAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        rvInspiration = view.findViewById(R.id.rvInspirationTrip);
        rvFreeDest = view.findViewById(R.id.rvFreeDestinations);
        btnIns = view.findViewById(R.id.btnIns);
        seeAllIns = view.findViewById(R.id.seeAllIns);

        fetchData();
        adapter = new HorizontalAdapter(this.getContext(),1);
        adapter.setOnItemClickListener(this);
        
        rvInspiration.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvInspiration.setAdapter(adapter);

        rvFreeDest.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFreeDest.setAdapter(adapter);

        btnIns.setOnClickListener(v -> {
            startActivity(new Intent(view.getContext(), ListTripActivity.class));
        });
        seeAllIns.setOnClickListener(v -> {
            startActivity(new Intent(view.getContext(), ListTripActivity.class));
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

                                listDest.add(places);
                            }
                            adapter.updateData(listDest);
                        } else {
                            Toast.makeText(getActivity(),"Fetch data failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onItemClick(Places place) {
        Intent intent = new Intent(this.getActivity(), PlaceDetailActivity.class);
        intent.putExtra("place",place);
        startActivity(intent);
    }
}