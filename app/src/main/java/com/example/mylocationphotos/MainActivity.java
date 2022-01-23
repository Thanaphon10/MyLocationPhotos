package com.example.mylocationphotos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton but_map = (ImageButton) findViewById(R.id.Button_map);
        ImageButton but_gal = (ImageButton) findViewById(R.id.Button_gallery);
        ImageButton but_came = (ImageButton) findViewById(R.id.Button_camera);

        but_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Maps_Atv = new Intent(MainActivity.this, Maps.class);
                startActivity(Maps_Atv);
            }
        });

        but_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Gal_Atv = new Intent(MainActivity.this, Gallery.class);
                startActivity(Gal_Atv);
            }
        });

        but_came.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Came_Atv = new Intent(MainActivity.this, Camera.class);
                startActivity(Came_Atv);
            }
        });
    }

}