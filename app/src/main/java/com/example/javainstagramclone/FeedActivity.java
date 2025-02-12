package com.example.javainstagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.javainstagramclone.databinding.ActivityFeedBinding;
import com.example.javainstagramclone.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class FeedActivity extends AppCompatActivity {

    private ActivityFeedBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);

        auth=FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_post){
            //Upload Activity
            Intent intentToUpload=new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);

        }
        else if(item.getItemId()==R.id.signout){
            //Sign Out
            auth.signOut();
            Intent intentToMain=new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intentToMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}