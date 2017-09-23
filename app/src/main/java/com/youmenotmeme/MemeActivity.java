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

public class MemeActivity extends AppCompatActivity {

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            imageUri = Uri.parse(extras.getString("imageUri"));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();
                ImageView memeView = (ImageView) findViewById(R.id.meme_display);
                memeView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
