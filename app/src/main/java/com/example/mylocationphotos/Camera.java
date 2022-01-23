package com.example.mylocationphotos;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Camera extends AppCompatActivity {

    static final int CAMERA = 10;

    private Button cam;
    private ImageView image;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        image = findViewById(R.id.but_image);
        cam = findViewById(R.id.cam);

        if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imagePath = createImage();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        startActivityForResult(intent, CAMERA);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imagePath = createImage();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(intent, CAMERA);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA) {
            if(resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Image captured successfully", Toast.LENGTH_SHORT).show();
                image.setImageURI(imageUri );
            }
        }
    }

    private Uri createImage() {
        Uri uri = null;
        ContentResolver resolver = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String imgName = String.valueOf(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgName + ".jpg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + "MLP/");
        Uri finalUri = resolver.insert(uri, contentValues);
        imageUri = finalUri;

        return finalUri;
    }
}