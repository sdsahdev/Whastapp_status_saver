package com.example.whastapp_saver_two;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageView extends AppCompatActivity {
    public android.widget.ImageView myImage;
    public android.widget.ImageView imBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        String file1 = intent.getStringExtra("file");
        File file = new File(file1);
        Log.d("check-files", "onCreate: file not create");

        imBack = findViewById(R.id.imBack);

        imBack.setOnClickListener(v -> {
            onBackPressed();
        });



//        if (file.exists()) {

//        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
        myImage = findViewById(R.id.image_view1);
//        Toast.makeText(this, "bitmapp " + file, Toast.LENGTH_SHORT).show();
//        Log.d("checkfiles", "onCreate: " + file);
//        myImage.setImageBitmap(myBitmap);

//        Picasso.get().load(String.valueOf(url)).into(binding.imgFromUserLocation);
        Glide.with(ImageView.this)
                .load(file1)
                .into(myImage);
//        }else{
//            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
//
//            Log.d("check-files", "onCreate: file not create");
//        }
    }
}