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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Camera extends AppCompatActivity {

    static final int CAMERA = 10;


    private Button cam,upimage,gallery;
    private ImageView image;

    String Country,Locality,Address,str_Latitude,str_Longitude;
    Double Latitude, Longitude;
    String currentPhotoPath;

    TextView TextView1,TextView2,TextView3,TextView4,TextView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase fAuth = FirebaseDatabase.getInstance();
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    StorageReference Sdata = FirebaseStorage.getInstance().getReference();

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        image = findViewById(R.id.but_image);
        cam = findViewById(R.id.cam);
        upimage = findViewById(R.id.upimage);
        gallery = findViewById(R.id.gallery);



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
        //image.setOnClickListener(imageOnClickListener);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,2);

                if(ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ getLocation();
                } else {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });


        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imagePath = createImage();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(intent,CAMERA);



                if(ActivityCompat.checkSelfPermission(Camera.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ getLocation();
                } else {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    uploadtofirebase(imageUri);
                }
                else{
                    Toast.makeText(Camera.this, "Pleas Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==2&& resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
        if (requestCode == CAMERA) {
            if(resultCode == Activity.RESULT_OK){
                image.setImageURI(imageUri);
                imageUri = data.getData();
            }
        }
    }

    private void uploadtofirebase(Uri uri){
        StorageReference fileRef = Sdata.child(System.currentTimeMillis()+"."+getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imgName = String.valueOf(System.currentTimeMillis());
                        Model model = new Model(uri.toString(),str_Latitude,str_Longitude,Country,Locality,Address,imgName);
                        //ส่งค่าเข้า firebase
                        root.child(imgName).setValue(model);

                        Toast.makeText(Camera.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Camera.this, "Uploading Failed !!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri mUri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }



    @SuppressLint("MissingPermission") //ระบุตำแหน่งปัจจุบัน
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

                        Latitude = addresses.get(0).getLatitude();
                        Longitude = addresses.get(0).getLongitude();
                        Country = addresses.get(0).getCountryName();
                        Locality = addresses.get(0).getLocality();
                        Address = addresses.get(0).getAddressLine(0);

                        str_Latitude = new StringBuilder().append(Latitude).toString();
                        str_Longitude = new StringBuilder().append(Longitude).toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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