package com.example.juliette.dchiffrage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AddScore extends AppCompatActivity {
    LinearLayout layout;
    ImageButton save;
    EditText name;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    String json;
    Gson gson;
    ArrayList<Partition> partitions;
    ArrayList<Bitmap> photos;
    ArrayList<Page> L;
    Type type = new TypeToken<List<Partition>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = prefs.edit();
        gson =  new Gson();
        partitions = new ArrayList<>();
        photos = new ArrayList<>();
        L = new ArrayList<>();

        if(prefs.contains("ListPartitions")) {
            json = prefs.getString("ListPartitions", "");
            partitions = gson.fromJson(json, type);
        }
        setContentView(R.layout.activity_add_partition);
        layout = findViewById(R.id.chosen);
        save = findViewById(R.id.ok);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Partition p = new Partition(name.getText().toString(), L);
                partitions.add(p);
                json = gson.toJson(partitions);
                prefsEditor.putString("ListPartitions",json);
                prefsEditor.commit();
                Intent intent = new Intent(AddScore.this, Accueil.class);
                startActivity(intent);
            }
        });
        name = findViewById(R.id.score_name);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri targetUri = data.getData();
                L.add(new Page(targetUri.getPath(),new ArrayList<Rectangle>()));
                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                Bitmap btm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imageView.setImageBitmap(btm);
                layout.addView(imageView);
                photos.add(btm);

                //ImageView linesView = new ImageView(this);
                //Hough hough = new Hough(btm);
                //Bitmap lines = hough.visionner();
                //linesView.setImageBitmap(lines);
                //layout.addView(linesView);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void save(Partition p) {
        partitions.add(p);
        json = gson.toJson(partitions);
        prefsEditor.putString("ListPartitions",json);
        prefsEditor.commit();
    }
}