package com.example.juliette.dchiffrage;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Jeu extends AppCompatActivity {
    int speed = 50;
    int measure = 0;
    ImageButton play,less, more, go;
    TextView tempo;
    EditText mesure;
    ViewPager mViewPager;
    Partition p;
    Gson gson;
    Type type = new TypeToken<List<Partition>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        gson = new Gson();
        p = gson.fromJson(intent.getStringExtra(Intent.EXTRA_TEXT), type);
        setContentView(R.layout.activity_jeu);
        Toolbar toolbar = findViewById(R.id.toolbar_jeu);
        setSupportActionBar(toolbar);
        play = findViewById(R.id.play);
        less = findViewById(R.id.less);
        more = findViewById(R.id.more);
        go = findViewById(R.id.go);
        tempo.setText(speed);
        mesure.setHint(measure);
        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed--;
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed++;
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measure = Integer.parseInt(mesure.getText().toString());
            }
        });
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(new MyAdapter(this,p));
    }
}
