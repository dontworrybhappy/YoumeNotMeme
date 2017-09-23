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
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static Uri file;
    private static Button buttonTakePhoto;
    private static Button buttonSelectPhoto;

    private Uri mImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSelectPhoto = (Button) findViewById(R.id.select_photo);
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getUserImages();
            }
        });
        buttonTakePhoto = (Button) findViewById(R.id.take_photo);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture();
            }
        });
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            buttonTakePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(MainActivity.this, new String [] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, CommonUtils.TAKE_PHOTO_CODE);
        }

    }

    private void getUserImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CommonUtils.READ_IMAGES_REQUEST);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = Uri.fromFile(getOutputMediaFile());
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CommonUtils.PHOTO_ACTIVITY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CommonUtils.READ_IMAGES_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                callWatson();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == CommonUtils.PHOTO_ACTIVITY && resultCode == RESULT_OK) {
            if(data != null) {
                mImageUri = Uri.parse(data.getExtras().getString("imageUri"));
            }
            try {
                callWatson();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CommonUtils.REQUEST_EXTERNAL_READ_PERMISSION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                ClassifyImageTask imageTask = new ClassifyImageTask();
                imageTask.execute();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        } else if(requestCode == CommonUtils.TAKE_PHOTO_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                buttonTakePhoto.setEnabled(true);
            }
        }

    }


    private static File getOutputMediaFile() {
        System.out.println("called this function getoutputmediafile");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        System.out.println("timestamPPPPPPP");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }
    /** see https://developer.android.com/training/volley/simple.html */
    private void callWatson() throws FileNotFoundException {
        Log.d("Requesting permissions", "requesting");
        CommonUtils.requestFilePermissions(this);
    }

    private void launchMemeActivity() {
        Intent i = new Intent(getApplicationContext(), MemeActivity.class);
        i.putExtra("imageUri", mImageUri.toString());
        startActivity(i);
    }

    private class ClassifyImageTask extends AsyncTask<Void, Void, Uri> {
        protected Uri doInBackground(Void... params) {
            final Uri imageUri = mImageUri;

            VisualRecognition service = new VisualRecognition(VisualRecognition
                    .VERSION_DATE_2016_05_20);
            service.setApiKey(getString(R.string.api_key));
            Log.d("Watson", imageUri.getPath());
            ClassifyOptions options = null;
            try {
//                Log.d("File", CommonUtils.getRealPathFromURI(getApplicationContext(), imageUri));

                options = new ClassifyOptions.Builder()
                        .imagesFile(new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_20170902-180308.png"))
                        .parameters("{\"classifier_ids\": [\"default\"]," +
                                "\"owners\": [\"IBM\"], \"threshold\": 0.4," +
                                "\"url\": \"https://staticdelivery.nexusmods.com/mods/110/images/74627-0-1459502036.jpg\"}")
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            try {
                ClassifiedImages result = service.classify(options).execute();
                Log.d("Watson", result.toString());
            } catch (BadRequestException e) {
                e.printStackTrace();
                return null;
            }

            return imageUri;
        }

        protected void onPostExecute(final Uri imageUri) {
            if (mImageUri != null) {
                launchMemeActivity();
            }
        }
    }
}