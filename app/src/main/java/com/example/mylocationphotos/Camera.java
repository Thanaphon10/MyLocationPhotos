package com.example.mylocationphotos;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Camera extends AppCompatActivity {

    static final int CAMERA = 10;

    private Button cam;
    private ImageView image;
    Uri imageUri;

    TextView TextView1,TextView2,TextView3,TextView4,TextView5;
    FusedLocationProviderClient fusedLocationProviderClient;

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


        TextView1 = findViewById(R.id.Text_view1);
        TextView2 = findViewById(R.id.Text_view2);
        TextView3 = findViewById(R.id.Text_view3);
        TextView4 = findViewById(R.id.Text_view4);
        TextView5 = findViewById(R.id.Text_view5);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imagePath = createImage();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(intent, CAMERA);

                if(ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ getLocation();
                } else {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
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


    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){

                    try {
                        Geocoder geocoder = new Geocoder(Camera.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                        TextView1.setText(Html.fromHtml("<font color='#6200EE'><b>Latitude : </b><br></font>" + addresses.get(0).getLatitude()));
                        TextView2.setText(Html.fromHtml("<font color='#6200EE'><b>Longitude : </b><br></font>" + addresses.get(0).getLongitude()));
                        TextView3.setText(Html.fromHtml("<font color='#6200EE'><b>Country name : </b><br></font>" + addresses.get(0).getCountryName()));
                        TextView4.setText(Html.fromHtml("<font color='#6200EE'><b>Locality : </b><br></font>" + addresses.get(0).getLocality()));
                        TextView5.setText(Html.fromHtml("<font color='#6200EE'><b>Address : </b><br></font>" + addresses.get(0).getAddressLine(0)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


}