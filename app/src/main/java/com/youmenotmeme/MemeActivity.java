package com.youmenotmeme;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.R.attr.bottom;
import static android.R.attr.button;

public class MemeActivity extends AppCompatActivity {

    String mImagePath;
    ArrayList<CaptionPair> mCaptions = new ArrayList<>();

    int memeNumber = 0;

    EditText mEditTextTop;
    EditText mEditTextBot;
    ImageView mImage;
    ProgressBar mProgressBar;

    Button buttonShare;
    Button buttonSave;
    ArrayList<String> urls = new ArrayList<String>();
    Button meme1;
    Button meme2;
    Button meme3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        buttonShare = (Button) findViewById(R.id.share);
        buttonShare.setOnTouchListener(new ButtonTouchListener(getDrawable(R.drawable.btn_save_pressed), buttonShare.getBackground()));
        buttonShare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                shareImage();

            }
        });
        buttonSave = (Button) findViewById(R.id.save);
        buttonSave.setOnTouchListener(new ButtonTouchListener(getDrawable(R.drawable.btn_save_pressed), buttonSave.getBackground()));
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                saveImageLocally();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        mEditTextTop = (EditText) findViewById(R.id.caption_top);
        mEditTextBot = (EditText) findViewById(R.id.caption_bottom);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        mEditTextTop.setTypeface(face);
        mEditTextTop.setEnabled(false);
        mEditTextBot.setTypeface(face);
        mEditTextBot.setEnabled(false);
        mImage = (ImageView) findViewById(R.id.meme_display);

        Drawable memePressedBackground = getDrawable(R.drawable.btn_meme_pressed);
        meme1 = (Button) findViewById(R.id.meme1);
        meme1.setOnTouchListener(new ButtonTouchListener(memePressedBackground, meme1.getBackground()));
        meme2 = (Button) findViewById(R.id.meme2);
        meme2.setOnTouchListener(new ButtonTouchListener(memePressedBackground, meme2.getBackground()));
        meme3 = (Button) findViewById(R.id.meme3);
        meme3.setOnTouchListener(new ButtonTouchListener(memePressedBackground, meme3.getBackground()));

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
                mEditTextBot.setText(mCaptions.get(memeNumber).bottom);
            }
        });

        meme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memeNumber = 3;
                mEditTextTop.setText(mCaptions.get(memeNumber).top);
                mEditTextBot.setText(mCaptions.get(memeNumber).bottom);
            }
        });

    }

    public Bitmap combineImages(Bitmap background, Bitmap top, Bitmap bot) {

        int width, height;
        Bitmap cs;

        mImage.setDrawingCacheEnabled(true);
        background = mImage.getDrawingCache();
        width = background.getWidth();
        height = background.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        System.out.println(height + " " + width + " " + top.getHeight() + " " + top.getWidth());
        top = Bitmap.createScaledBitmap(top, width, height, true);
        bot = Bitmap.createScaledBitmap(bot, width, height, true);

        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(top, 0, 0, null);
        comboImage.drawBitmap(bot, 0, 0, null);

        return cs;
    }

    /**
     * https://stackoverflow.com/questions/2339429/android-view-getdrawingcache-returns-null-only-null
     */
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }

    /**
     * Next 2 functions taken from https://stackoverflow.com/questions/7661875/how-to-use-share-image-using-sharing-intent-to-share-images-in-android
     */
    private void shareImage() {
        callCombineImages(true);
    }

    private void callCombineImages(final boolean share) {
        ViewTreeObserver vto = mEditTextTop.getViewTreeObserver();
        Bitmap top = loadBitmapFromView(mEditTextTop);
        Bitmap bot = loadBitmapFromView(mEditTextBot);
        Bitmap background = CommonUtils.createBitmapFromPath(mImagePath);

        Bitmap combinedBmp = combineImages(background, top, bot);

        mEditTextTop.setVisibility(View.INVISIBLE);
        mEditTextBot.setVisibility(View.INVISIBLE);
        mImage.setImageBitmap(combinedBmp);
        Log.d("Meme", "Made combined");
        Log.d("Meme", mEditTextTop.getText().toString());
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG",
                    "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            Log.d("PICTURE", pictureFile.getPath());
            if (urls.size() == 0) {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                combinedBmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                urls.add(pictureFile.getPath());
                if (!share) {
                    Toast.makeText(MemeActivity.this, "Saved", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MemeActivity.this, "Already Saved", Toast.LENGTH_LONG).show();
            }
            if (share) {
                String localAbsoluteFilePath = "";
                System.out.println("urls size " + urls.size());
                if (urls.size() == 0) {
                    localAbsoluteFilePath = pictureFile.getPath();
                } else {
                    localAbsoluteFilePath = urls.get(urls.size() - 1);
                }

                System.out.println("filepath: " + localAbsoluteFilePath);

                if (localAbsoluteFilePath != null && !localAbsoluteFilePath.equals("")) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/jpg");
                    File f = new File(localAbsoluteFilePath);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
                    mProgressBar.setVisibility(View.INVISIBLE);
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                }
            }
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
    }

    private void saveImageLocally() {
        callCombineImages(false);
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
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + getString(R.string.target_path));
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
