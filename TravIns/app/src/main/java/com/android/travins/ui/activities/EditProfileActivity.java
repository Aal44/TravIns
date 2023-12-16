package com.android.travins.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.travins.R;
import com.android.travins.databinding.ActivityEditProfileBinding;
import com.android.travins.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        String id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String username = getIntent().getStringExtra("username");
        String gender = getIntent().getStringExtra("gender");
        String email = getIntent().getStringExtra("email");
        String country = getIntent().getStringExtra("country");
        String bod = getIntent().getStringExtra("bod");
        String address = getIntent().getStringExtra("address");

        binding.edtTxtFullName.setText(name);
        binding.edtTxtAddress.setText(address);
        binding.edtTxtBod.setText(bod);
        binding.edtTxtCountry.setText(country);
        binding.edtTxtGender.setText(gender);
        binding.edtTxtUsername.setText(username);
        binding.edtTxtFullName.setText(name);

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            CollectionReference usersCollection = db.collection("users");
            assert id != null;
            DocumentReference userDocument = usersCollection.document(id);
            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", binding.edtTxtFullName.getText().toString());
            updates.put("username", binding.edtTxtUsername.getText().toString());
            updates.put("address", binding.edtTxtAddress.getText().toString());
            updates.put("country", binding.edtTxtCountry.getText().toString());
            updates.put("gender", binding.edtTxtGender.getText().toString());
            updates.put("bod", binding.edtTxtBod.getText().toString());
            userDocument.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this,"Update successful!",Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this,"Update failed!",Toast.LENGTH_SHORT).show();
                        // Update failed
                        // Handle the error
                    });
        });

    }
}