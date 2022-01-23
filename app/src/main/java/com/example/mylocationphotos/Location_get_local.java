package com.example.mylocationphotos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location_get_local extends AppCompatActivity {

    Button  BT_Get_location;
    TextView TextView1,TextView2,TextView3,TextView4,TextView5;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_get_local);


        BT_Get_location = findViewById(R.id.BT_get_location);
        TextView1 = findViewById(R.id.Text_view1);
        TextView2 = findViewById(R.id.Text_view2);
        TextView3 = findViewById(R.id.Text_view3);
        TextView4 = findViewById(R.id.Text_view4);
        TextView5 = findViewById(R.id.Text_view5);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        BT_Get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(Location_get_local.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(Location_get_local.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }


        });

        if(ActivityCompat.checkSelfPermission(Location_get_local.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        } else {
            ActivityCompat.requestPermissions(Location_get_local.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


    }
    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){

                    try {
                        Geocoder geocoder = new Geocoder(Location_get_local.this, Locale.getDefault());
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