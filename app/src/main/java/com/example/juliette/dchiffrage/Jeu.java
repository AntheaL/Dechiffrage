package com.example.juliette.dchiffrage;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
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
import java.text.ParseException;
import java.util.List;

public class Jeu extends AppCompatActivity {
    int speed = 50;
    int measure = 0;
    ImageButton play,less, more, go;
    TextView tempo;
    EditText mesure;
    Partition p;
    Gson gson;
    Type type = new TypeToken<List<Partition>>(){}.getType();
    String json;
    FragmentTransaction ft;
    Swipe swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ft  = getSupportFragmentManager().beginTransaction();
        swipe = new Swipe();
        ft.add(R.id.placeholder, swipe);
        ft.commit();
        Intent intent_2 = getIntent();
        gson = new Gson();
        json = intent_2.getStringExtra(Intent.EXTRA_TEXT);
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
                try {
                    ft.replace(R.id.placeholder, new Swipe());
                    ft.commit();
                    measure = Integer.parseInt(mesure.getText().toString());
                    swipe.adapter.instantiateItem(swipe.mViewPager,measure-1);
                }
                catch(NumberFormatException e) {
                }
            }
        });
   }
}
