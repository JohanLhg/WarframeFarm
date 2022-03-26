package com.warframefarm.activities.startup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.warframefarm.activities.main.MainActivity;
import com.warframefarm.databinding.ActivityStartUpBinding;

public class StartUpActivity extends AppCompatActivity {

    private ActivityStartUpBinding binding;
    private StartUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStartUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication())
                .create(StartUpViewModel.class);

        //Setup DB if not already done
        //Check for connection
        //if yes
            //Check for updates
            //Get online data
            //Apply changes

        //When everything is done -> start main

        viewModel.isLoading().observe(this, isLoading -> {
            if (!isLoading) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}