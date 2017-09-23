package com.youmenotmeme;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
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


import java.io.IOException;
import java.util.ArrayList;

public class MemeActivity extends AppCompatActivity {

    Uri mImageUri;
    ArrayList<String> mCaptions = new ArrayList<String>();

    EditText mEditText;
    ImageView mImage;
    ImageView mCombined;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();

        mEditText = (EditText) findViewById(R.id.caption_top);
        mImage = (ImageView) findViewById(R.id.meme_display);
        mCombined = (ImageView) findViewById(R.id.combined_image);

        if (extras != null) {
            mImageUri = Uri.parse(extras.getString("imageUri"));
            mCaptions = extras.getStringArrayList("captions");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                Toast.makeText(this, mImageUri.toString(), Toast.LENGTH_LONG).show();
                mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ViewTreeObserver vto = mEditText.getViewTreeObserver();
            vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mEditText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    try {
                        Bitmap foreground = loadBitmapFromView(mEditText);
                        Bitmap background = MediaStore.Images.Media.getBitmap(MemeActivity.this.getContentResolver(), mImageUri);

                        Bitmap combinedBmp = combineImages(background, foreground);

                        mCombined.setImageBitmap(combinedBmp);
                        mEditText.setVisibility(View.INVISIBLE);
                        mImage.setVisibility(View.INVISIBLE);
                        mCombined.setVisibility(View.VISIBLE);
                        Log.d("Meme", "Made combined");
                        Log.d("Meme", mEditText.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
}
