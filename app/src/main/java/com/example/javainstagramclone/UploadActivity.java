package com.example.javainstagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.javainstagramclone.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFireStore;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);

        registerLauncher();

        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFireStore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

    }

    public void uploadButtonClick(View view) {

        if(imageData!=null){
            //Universal Unique ID = UUID
            UUID uuid=UUID.randomUUID();
            String imageName="images/"+uuid+".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //download url
                    StorageReference newReference=firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();
                            String comment=binding.commentText.getText().toString();
                            FirebaseUser user=auth.getCurrentUser();
                            String email=user.getEmail();

                            HashMap<String,Object> postData=new HashMap<>();
                            postData.put("useremail",email);
                            postData.put("downloaadurl",downloadUrl);
                            postData.put("comment",comment);
                            postData.put("date", FieldValue.serverTimestamp());


                            firebaseFireStore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent=new Intent(UploadActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void selectImage(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!=PackageManager.PERMISSION_GRANTED){
                //İzin verilmedi izin isteme mantığını açıkla
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission",new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            //İzin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }else{
                    //İzin verilmedi izin iste
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else{
                //İzin verildi galeriye git
                Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }
        else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //İzin verilmedi izin isteme mantığını açıkla
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission",new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            //İzin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }else{
                    //İzin verilmedi izin iste
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else{
                //İzin verildi galeriye git
                Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }


    }

    private void registerLauncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult!=null){
                        imageData=intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
                    }
                }

            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //İzin verildi galeriye git
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else{
                    Toast.makeText(UploadActivity.this,"Permission needed",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



}

