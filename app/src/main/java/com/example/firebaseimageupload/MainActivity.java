package com.example.firebaseimageupload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;
    private Uri fileUri;
    Button choose,upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        choose=findViewById(R.id.btnChooseImg);
        upload=findViewById(R.id.btnUploadImg);

        choose.setOnClickListener(view -> openFileChooser());

        upload.setOnClickListener(view -> uploadImage(fileUri));

    }

    private void uploadImage(Uri fileUri) {

        if (fileUri != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("Images");
            StorageReference imageRef=storageRef.child("image"+fileUri.getLastPathSegment());

            UploadTask uploadTask = imageRef.putFile(fileUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl=uri.toString();
                    storeImageUrl(imageUrl);
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(exception -> {
                Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show();
            });
        }else {
            Toast.makeText(this, "No file Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void storeImageUrl(String imageUrl) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imageDatabaseRef=database.getReference("images");
        String key = imageDatabaseRef.getKey().toString();
        imageDatabaseRef.child(key).setValue(imageUrl);

    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            fileUri=data.getData();
        }
    }
}