package com.youmenotmeme;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MemeActivity extends AppCompatActivity {

    String mImagePath;
    ArrayList<String> mCaptions = new ArrayList<String>();

    EditText mEditText;
    ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();

        mEditText = (EditText) findViewById(R.id.caption_top);
        mImage = (ImageView) findViewById(R.id.meme_display);

        if (extras != null) {
            mImagePath = extras.getString("imagePath");
            mCaptions = extras.getStringArrayList("captions");

//            Bitmap bitmap = CommonUtils.createBitmapFromPath(mImagePath);
//            Toast.makeText(this, mImagePath, Toast.LENGTH_LONG).show();
//            mImage.setImageBitmap(bitmap);

            ViewTreeObserver vto = mEditText.getViewTreeObserver();
            vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mEditText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Bitmap foreground = loadBitmapFromView(mEditText);
                    Bitmap background = CommonUtils.createBitmapFromPath(mImagePath);

                    Bitmap combinedBmp = combineImages(background, foreground);

                    mEditText.setVisibility(View.INVISIBLE);
                    mImage.setImageBitmap(combinedBmp);
                    Log.d("Meme", "Made combined");
                    Log.d("Meme", mEditText.getText().toString());
                }
            });


            mEditText.setText("Hello World");
        }

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

    private String saveImageLocally(Bitmap _bitmap) {
//
//        File outputDir = Utils.getAlbumStorageDir(Environment.DIRECTORY_DOWNLOADS);
//        File outputFile = null;
//        try {
//            outputFile = File.createTempFile("tmp", ".png", outputDir);
//        } catch (IOException e1) {
//            // handle exception
//        }
//
//        try {
//            FileOutputStream out = new FileOutputStream(outputFile);
//            _bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.close();
//
//        } catch (Exception e) {
//            // handle exception
//        }
//
//        return outputFile.getAbsolutePath();
        return "";
    }
}
