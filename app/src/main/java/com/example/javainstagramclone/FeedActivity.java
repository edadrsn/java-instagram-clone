package com.example.javainstagramclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.javainstagramclone.databinding.ActivityFeedBinding;
import com.example.javainstagramclone.databinding.ActivityMainBinding;

public class FeedActivity extends AppCompatActivity {

    private ActivityFeedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.myToolbar);
    }
}