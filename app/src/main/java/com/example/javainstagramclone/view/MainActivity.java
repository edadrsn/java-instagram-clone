package com.example.javainstagramclone.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javainstagramclone.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        //Daha önce giriş yapılmış mı kontrol et
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Kullanıcı giriş yapmışsa FeedActivity'e yönlendir
    public void signInClicked(View view){
        String email=binding.email.getText().toString();
        String password=binding.password.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(MainActivity.this,"Enter email and password",Toast.LENGTH_LONG).show();
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    //Kullanıcı kayıt olmuşsa FeedActivity'e yönlendir
    public void signUpClicked(View view){
        String email=binding.email.getText().toString();
        String password=binding.password.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(MainActivity.this,"Enter email and password", Toast.LENGTH_LONG).show();
        }
        else {
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}