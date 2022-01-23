package com.example.mylocationphotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Gallery extends AppCompatActivity {

        private static final int RQS_OPEN_IMAGE = 1;

        Button buttonOpen;
        TextView textUri;
        TextView textExif;
        ImageView imageView;

        Uri targetUri = null;
        String path = "null";

        View.OnClickListener buttonOpenOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, RQS_OPEN_IMAGE);

            }

        };


        void showExif(Uri photoUri){
            if(photoUri != null){

                ParcelFileDescriptor parcelFileDescriptor = null;

            /*
            How to convert the Uri to FileDescriptor, refer to the example in the document:
            https://developer.android.com/guide/topics/providers/document-provider.html
             */
                try {
                    parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                /*
                ExifInterface (FileDescriptor fileDescriptor) added in API level 24
                 */
                    ExifInterface exifInterface = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        exifInterface = new ExifInterface(fileDescriptor);
                    }
                    String exif="Exif: " + fileDescriptor.toString();
                    exif += "\n IMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                    exif += "\n IMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                    exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                    exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                    exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                    exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                    exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                    exif += "\n GPS related:";
                    exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                    exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                    exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

                    parcelFileDescriptor.close();


                    //Toast.makeText(getApplicationContext(), exif, Toast.LENGTH_LONG).show();

                    textExif.setText(exif);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something wrong:\n" + e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something wrong:\n" + e.toString(), Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(getApplicationContext(), "photoUri == null", Toast.LENGTH_LONG).show();
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gallery);

            buttonOpen = (Button) findViewById(R.id.opendocument);
            buttonOpen.setOnClickListener(buttonOpenOnClickListener);

            //textUri = (TextView) findViewById(R.id.texturi);

            textExif = (TextView) findViewById(R.id.textView_Exif);

            imageView = (ImageView)findViewById(R.id.image);
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Gallery.RESULT_OK) {

                Uri dataUri = data.getData();

                if (requestCode == RQS_OPEN_IMAGE) {
                    targetUri = dataUri;
                    //textUri.setText(dataUri.toString());
                    path = dataUri.toString();  //textExif.setText(path);

                    try {
                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                        imageView.setImageBitmap(bm);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    showExif(targetUri);

                }

            }

        }
    }