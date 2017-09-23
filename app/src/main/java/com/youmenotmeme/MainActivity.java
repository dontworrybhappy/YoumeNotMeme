package com.youmenotmeme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 7;
    private static final int TAKE_PHOTO_CODE = 100;
    private static Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonSelectPhoto = (Button) findViewById(R.id.select_photo);
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getUserImages();
            }
        });
        final Button buttonTakePhoto = (Button) findViewById(R.id.take_photo);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture();
            }
        });
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            buttonTakePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(MainActivity.this, new String [] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                final Button buttonTakePhoto = (Button) findViewById(R.id.select_photo);
                buttonTakePhoto.setEnabled(true);
            }
        }
    }

    private void getUserImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void takePicture() {
        Toast.makeText(MainActivity.this, "happy no toast", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(MainActivity.this, "happy more toast", Toast.LENGTH_LONG).show();
        file = Uri.fromFile(getOutputMediaFile());
        Toast.makeText(MainActivity.this, "happy yes toast", Toast.LENGTH_LONG).show();
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        Toast.makeText(MainActivity.this, "happy my toast", Toast.LENGTH_LONG).show();
        //startActivityForResult(intent, TAKE_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == READ_REQUEST_CODE) {
            Uri imageUri = data.getData();
            Intent i = new Intent(getApplicationContext(), MemeActivity.class);
            i.putExtra("imageUri", imageUri.toString());
            startActivity(i);
        }
        if (requestCode == TAKE_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                    ImageView imageView = (ImageView) findViewById(R.id.dankassmemes);
                    imageView.setImageBitmap(bitmap);
                    imageView.setImageURI(file);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

}
