package com.youmenotmeme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Class;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImage;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Classifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Uri mImageUri = null;
    private static final double CLASS_THRESHOLD = 0.4;

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
                takePhoto();
            }
        });
    }

    private void getUserImages() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            CommonUtils.requestFilePermissions(this);
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CommonUtils.READ_IMAGES_REQUEST);
    }

    private void takePhoto() {

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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CommonUtils.REQUEST_EXTERNAL_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getUserImages();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /** see https://developer.android.com/training/volley/simple.html */
    private void callWatson() throws FileNotFoundException {
        ClassifyImageTask imageTask = new ClassifyImageTask();
        imageTask.execute();
    }

    private void launchMemeActivity(ArrayList<String> captions) {
        Intent i = new Intent(getApplicationContext(), MemeActivity.class);
        i.putExtra("imageUri", mImageUri.toString());
        i.putStringArrayListExtra("captions", captions);
        startActivity(i);
    }

    private class ClassifyImageTask extends AsyncTask<Void, Void, ClassifiedImages> {
        protected ClassifiedImages doInBackground(Void... params) {
            final Uri imageUri = mImageUri;

            VisualRecognition service = new VisualRecognition(VisualRecognition
                    .VERSION_DATE_2016_05_20);
            service.setApiKey(getString(R.string.api_key));
            Log.d("Watson", imageUri.getPath());
            ClassifyOptions options;
            ClassifiedImages result;
            try {
                options = new ClassifyOptions.Builder()
                        .imagesFile(new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_20170902-180308.png"))
                        .parameters("{\"classifier_ids\": [\"" + getString(R.string.meme_classifier) + "\"]," +
                                "\"owners\": [\"me\"]}")
                        .build();
                result = service.classify(options).execute();
            } catch (BadRequestException|FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            return result;
        }

        protected void onPostExecute(final ClassifiedImages result) {
            if (result != null) {
                ArrayList<String> captions = parseWatsonResult(result);

                if (captions != null && !captions.isEmpty()) {
                    launchMemeActivity(captions);
                }
            }
        }

        private ArrayList<String> parseWatsonResult(ClassifiedImages result) {
            List<ClassifiedImage> images = result.getImages();
            if (images.size() != 1) {
                Log.d("captions", images.toString());
                return null;
            }
            ClassifiedImage image =  images.get(0);

            List<ClassifierResult> classifiers = image.getClassifiers();
            if (classifiers.size() != 1 || !classifiers.get(0).getClassifierId().equals(getString(R.string.meme_classifier))) {
                Log.d("captions", classifiers.get(0).getClassifierId() + getString(R.string.meme_classifier));
                return null;
            }
            List<ClassResult> classes = classifiers.get(0).getClasses();

            ArrayList<String> captions = new ArrayList<>();
            for (ClassResult classResult : classes) {
                if (classResult.getScore() > CLASS_THRESHOLD) {
                    captions.add(classResult.getClassName());
                }
            }
            Log.d("captions", captions.toString());

            return captions;
        }
    }
}
