package com.youmenotmeme;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;

public class MemeActivity extends AppCompatActivity {

    Uri mImageUri;
    ArrayList<String> mCaptions = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mImageUri = Uri.parse(extras.getString("imageUri"));
            mCaptions = extras.getStringArrayList("captions");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                Toast.makeText(this, mImageUri.toString(), Toast.LENGTH_LONG).show();
                ImageView memeView = (ImageView) findViewById(R.id.meme_display);
                memeView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

}
