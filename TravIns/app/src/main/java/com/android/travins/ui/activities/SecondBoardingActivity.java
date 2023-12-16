package com.android.travins.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.android.travins.databinding.ActivitySecondBoardingBinding;

public class SecondBoardingActivity extends AppCompatActivity {


    private ActivitySecondBoardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> {
            Intent mainIntent = new Intent(SecondBoardingActivity.this, LoginActivity.class);
            startActivity(mainIntent);
        });

        binding.btnRegister.setOnClickListener(v -> {
            Intent mainIntent = new Intent(SecondBoardingActivity.this, RegisterActivity.class);
            startActivity(mainIntent);
        });
    }
}