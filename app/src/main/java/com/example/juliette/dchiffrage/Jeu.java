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
import java.text.ParseException;
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
    MyAdapter adapter;
    Type type = new TypeToken<List<Partition>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MyAdapter(this, p);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        Intent intent_2 = getIntent();
        gson = new Gson();
        p = gson.fromJson(intent_2.getStringExtra(Intent.EXTRA_TEXT), type);
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
                    measure = Integer.parseInt(mesure.getText().toString());
                    int[] pos = positions(measure);
                    adapter.instantiateItem(mViewPager,pos[0],pos[1]);
                }
                catch(NumberFormatException e) {
                }
            }
        });
   }

    public int[] positions(int measure) { // the page and measure returned start from 0
        int n = 1;
        int p = -1;
        do {
            for (Page page : p) {
                n += page.mesures.size();
                p++;
            }
        }
        while(n<measure);
        return new int[] {p,measure-n};
    }
}
