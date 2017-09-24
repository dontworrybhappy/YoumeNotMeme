package com.youmenotmeme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImage;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierResult;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static Uri file;
    private static Button buttonTakePhoto;
    private static Button buttonSelectPhoto;
    private static Button buttonHistory;
    private static ProgressBar progressBar;

    private Uri mImageUri = null;
    private String mImagePath = null;

    private static final double CLASS_THRESHOLD = 0.4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSelectPhoto = (Button) findViewById(R.id.select_photo);
        buttonSelectPhoto.setOnTouchListener(new ButtonTouchListener(getDrawable(R.drawable.btn_large_create_pressed), buttonSelectPhoto.getBackground()));
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getUserImages();
            }
        });
        buttonHistory = (Button) findViewById(R.id.photo_history);
        buttonHistory.setOnTouchListener(new ButtonTouchListener(getDrawable(R.drawable.btn_large_takephoto_pressed), buttonHistory.getBackground()));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(i);
            }
        });
        buttonTakePhoto = (Button) findViewById(R.id.take_photo);
        buttonTakePhoto.setOnTouchListener(new ButtonTouchListener(getDrawable(R.drawable.btn_large_takephoto_pressed), buttonTakePhoto.getBackground()));
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

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                callWatsonFromBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(requestCode == CommonUtils.PHOTO_ACTIVITY && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            callWatsonFromBitmap(bitmap);
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
                getUserImages();

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

    /** see https://developer.android.com/training/volley/simple.html */
    private void callWatson() throws FileNotFoundException {
        progressBar.setVisibility(View.VISIBLE);
        ClassifyImageTask imageTask = new ClassifyImageTask();
        imageTask.execute();
    }

    private void launchMemeActivity(ArrayList<CaptionPair> captions) {
        progressBar.setVisibility(View.INVISIBLE);
        Intent i = new Intent(getApplicationContext(), MemeActivity.class);
        System.out.println("image path" + mImagePath);
        for(int x = 0; x < captions.size(); x++) {
            System.out.println("captions" + captions.get(x).toString());
        }
        i.putExtra("imagePath", mImagePath);
        i.putParcelableArrayListExtra("captions", captions);
        startActivity(i);
    }

    private class ClassifyImageTask extends AsyncTask<Void, Void, ClassifiedImages> {
        protected ClassifiedImages doInBackground(Void... params) {


            VisualRecognition service = new VisualRecognition(VisualRecognition
                    .VERSION_DATE_2016_05_20);
            service.setApiKey(getString(R.string.api_key));
            ClassifyOptions options;
            ClassifiedImages result;
            try {
                System.out.println("PATH" + mImagePath);
                options = new ClassifyOptions.Builder()
                        .imagesFile(new File(mImagePath))
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
                ArrayList<CaptionPair> captions = parseWatsonResult(result);

                if (captions != null && !captions.isEmpty()) {
                    launchMemeActivity(captions);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        }

        private ArrayList<CaptionPair> parseWatsonResult(ClassifiedImages result) {
            List<ClassifiedImage> images = result.getImages();
            if (images.size() != 1) {
                Log.d("captions", images.toString());
                return null;
            }
            ClassifiedImage image =  images.get(0);
            Log.d("Image Results", image.toString());

            List<ClassifierResult> classifiers = image.getClassifiers();
            if (classifiers == null || classifiers.size() != 1 || !classifiers.get(0).getClassifierId().equals(getString(R.string.meme_classifier))) {
                if (image.getError() != null && image.getError().getErrorId().equals("input_error")) {
                    Toast.makeText(MainActivity.this, "Try another image, max file size is 2MB.", Toast.LENGTH_SHORT).show();
                    return null;
                }

                return Captions.captions.get("wat");
            }
            List<ClassResult> classes = classifiers.get(0).getClasses();

            ArrayList<CaptionPair> captions = new ArrayList<>();
            ClassResult highestClass = null;
            for (ClassResult classResult : classes) {
                if (highestClass == null || classResult.getScore() > highestClass.getScore()) {
                    highestClass = classResult;
                }
            }
            if (highestClass == null) {
                captions = Captions.captions.get("wat");
            } else {
                captions = Captions.captions.get(highestClass.getClassName());
            }

            return captions;
        }
    }

    private void callWatsonFromBitmap(Bitmap bmp) {
        try {
            File tmpFile = File.createTempFile(
                "youmetmp",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.close();
            mImagePath = tmpFile.getPath();
            callWatson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ButtonTouchListener implements View.OnTouchListener {
        private Drawable mPressedBg;
        private Drawable mUnpressedBg;

        ButtonTouchListener(Drawable pressedBg, Drawable unpressedBg) {
            mPressedBg = pressedBg;
            mUnpressedBg = unpressedBg;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Button btn = (Button) view;
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                btn.setBackground(mPressedBg);
                btn.setTextColor(getColor(R.color.pressedText));
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                btn.setBackground(mUnpressedBg);
                btn.setTextColor(getColor(R.color.unpressedText));
            }
            return false;
        }
    }
}
