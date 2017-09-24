package com.youmenotmeme;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MemeActivity extends AppCompatActivity {

    String mImagePath;
    ArrayList<CaptionPair> mCaptions = new ArrayList<>();

    int memeNumber = 0;

    EditText mEditTextTop;
    EditText mEditTextBot;
    ImageView mImage;

    Button buttonShare;
    Button buttonSave;
    Button meme1;
    Button meme2;
    Button meme3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();


        buttonShare = (Button) findViewById(R.id.share);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareImage(((BitmapDrawable)mImage.getDrawable()).getBitmap());
            }
        });
        buttonSave = (Button) findViewById(R.id.save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveImageLocally(((BitmapDrawable)mImage.getDrawable()).getBitmap());
            }
        });
        mEditTextTop = (EditText) findViewById(R.id.caption_top);
        mEditTextBot = (EditText) findViewById(R.id.caption_bottom);
        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/impact.ttf");
        mEditTextTop.setTypeface(face);
        mEditTextTop.setEnabled(false);
        mEditTextBot.setTypeface(face);
        mEditTextBot.setEnabled(false);
        mImage = (ImageView) findViewById(R.id.meme_display);
        meme1 = (Button) findViewById(R.id.meme1);
        meme2 = (Button) findViewById(R.id.meme2);
        meme3 = (Button) findViewById(R.id.meme3);

        if (extras != null) {
            mImagePath = extras.getString("imagePath");
            mCaptions = extras.getParcelableArrayList("captions");
            Collections.shuffle(mCaptions);

            Bitmap bitmap = CommonUtils.createBitmapFromPath(mImagePath);
            mImage.setImageBitmap(bitmap);

            mEditTextTop.setText(mCaptions.get(memeNumber).top);
            mEditTextBot.setText(mCaptions.get(memeNumber).bottom);
        }

        meme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memeNumber = 1;
                mEditTextTop.setText(mCaptions.get(memeNumber).top);
                mEditTextBot.setText(mCaptions.get(memeNumber).bottom);
            }
        });

        meme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memeNumber = 2;
                mEditTextTop.setText(mCaptions.get(memeNumber).top);
                mEditTextBot.setText(mCaptions.get(memeNumber).bottom);            }
        });

        meme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memeNumber = 3;
                mEditTextTop.setText(mCaptions.get(memeNumber).top);
                mEditTextBot.setText(mCaptions.get(memeNumber).bottom);            }
        });

    }

    public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        int width, height;
        Bitmap cs;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        width = size.x;
        height = size.y;

        System.out.println("bmp" + foreground.getHeight());


        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(foreground, 0, 0, null);

        return cs;
    }

    /** https://stackoverflow.com/questions/2339429/android-view-getdrawingcache-returns-null-only-null */
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    /** Next 2 functions taken from https://stackoverflow.com/questions/7661875/how-to-use-share-image-using-sharing-intent-to-share-images-in-android */
    private void shareImage(Bitmap bitmapImage) {
        callCombineImages();
        String localAbsoluteFilePath = saveImageLocally(bitmapImage);

        if (localAbsoluteFilePath != null && !localAbsoluteFilePath.equals("")) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri phototUri = Uri.parse(localAbsoluteFilePath);

            File file = new File(phototUri.getPath());

            Log.d("TAG", "file path: " + file.getPath());

            if (file.exists()) {
                // file create success

            } else {
                // file create fail
            }
            shareIntent.setData(phototUri);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, phototUri);

        }
    }

    private void callCombineImages() {
        ViewTreeObserver vto = mEditTextTop.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEditTextTop.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Bitmap foreground = loadBitmapFromView(mEditTextTop);
                Bitmap background = CommonUtils.createBitmapFromPath(mImagePath);

                Bitmap combinedBmp = combineImages(background, foreground);

                mEditTextTop.setVisibility(View.INVISIBLE);
                mImage.setImageBitmap(combinedBmp);
                Log.d("Meme", "Made combined");
                Log.d("Meme", mEditTextTop.getText().toString());
            }
        });
    }

    private String saveImageLocally(Bitmap image) {
        callCombineImages();
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG",
                    "Error creating media file, check storage permissions: ");
            return"";
        }
        try {
            Log.d("PICTURE", pictureFile.getPath());
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
        return "";
    }

    private void galleryAddPic(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(getString(R.string.target_path));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        galleryAddPic(mediaFile);
        return mediaFile;
    }
}
