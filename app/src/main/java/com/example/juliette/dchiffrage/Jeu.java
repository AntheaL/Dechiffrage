package com.example.juliette.dchiffrage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    Scroll scroll;
    ArrayList<Partition> partitions;

    //ViewPager mViewPager; // Attributs sensés être dans Swipe
    //MyAdapter adapter;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu);
        Toolbar toolbar = findViewById(R.id.toolbar_jeu);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        partitions = new ArrayList<>();

        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        gson = new Gson();
        json = prefs.getString("ListPartitions", "");
        partitions = gson.fromJson(json, type);
        int k = Integer.parseInt(intent.getStringExtra("Partition"));
        p = partitions.get(k);

        // json = intent.getStringExtra("Partition");
        // p = gson.fromJson(json,type);

        /* adapter = new MyAdapter(this, p);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);

        measure = mViewPager.getCurrentItem() +1 ; */
        ft  = getSupportFragmentManager().beginTransaction();

        scroll = Scroll.newInstance(gson.toJson(p));
        ft.add(R.id.placeholder, scroll);
        ft.commit();


        play = findViewById(R.id.play);
        less = findViewById(R.id.less);
        more = findViewById(R.id.more);
        go = findViewById(R.id.go);
        tempo = findViewById(R.id.tempo);
        mesure = findViewById(R.id.mesure);
        tempo.setText(String.valueOf(speed));
        mesure.setHint(String.valueOf(measure));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.replace(R.id.placeholder, scroll);
                ft.commit();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed++;
            }
        });
        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speed--;
            }
        });

        /* go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ft.replace(R.id.placeholder, Swipe.newInstance());
                    ft.commit();
                    measure = Integer.parseInt(mesure.getText().toString());
                    adapter.instantiateItem(mViewPager,measure-1);
                }
                catch(NumberFormatException e) {
                }
            }
        }); */
   }

}
