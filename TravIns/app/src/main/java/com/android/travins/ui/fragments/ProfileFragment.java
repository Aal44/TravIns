package com.android.travins.ui.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.travins.R;
import com.android.travins.data.models.User;
import com.android.travins.ui.activities.EditProfileActivity;
import com.android.travins.ui.activities.ListTripActivity;
import com.android.travins.ui.activities.LoginActivity;
import com.android.travins.ui.activities.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    protected FirebaseFirestore db;
    protected FirebaseAuth auth;
    protected User user = new User();

    TextView fullName;
    TextView goToEditProfile;

    ImageView btnLogout;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fullName = view.findViewById(R.id.profileFullName);
        goToEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(view.getContext(), LoginActivity.class));
            this.getActivity().finish();
        });

        goToEditProfile.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this.getActivity(), EditProfileActivity.class);
            mainIntent.putExtra("id",user.getId());
            mainIntent.putExtra("name",user.getFullName());
            mainIntent.putExtra("username",user.getUsername());
            mainIntent.putExtra("email",user.getEmail());
            mainIntent.putExtra("gender",user.getGender());
            mainIntent.putExtra("country",user.getCountry());
            mainIntent.putExtra("bod",user.getBod());
            mainIntent.putExtra("address",user.getAddress());
            startActivity(mainIntent);
        });
        db.collection("users").whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                fullName.setText(data.get("fullName").toString());
                                user = new User(document.getId(),(String) data.get("fullName"),
                                        (String) data.get("username"), (String) data.get("email"),
                                        (String) data.get("password"), (String) data.get("gender"),
                                        (String) data.get("country"), (String) data.get("bod"),
                                        (String) data.get("address"));
                            }
                        } else {
                            Log.w("ERROR", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}