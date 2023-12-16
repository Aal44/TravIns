package com.android.travins.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.travins.R;
import com.android.travins.databinding.ActivityFirstBoardingBinding;

public class FirstBoardingActivity extends AppCompatActivity {

    private ActivityFirstBoardingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnContinue.setOnClickListener(v -> {
            Intent mainIntent = new Intent(FirstBoardingActivity.this, SecondBoardingActivity.class);
            startActivity(mainIntent);
        });
    }
}