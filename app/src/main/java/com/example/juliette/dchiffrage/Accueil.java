package com.example.juliette.dchiffrage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.EXTRA_COMPONENT_NAME;

public class Accueil extends AppCompatActivity {

    Type type = new TypeToken<List<Partition>>(){}.getType();
    TableLayout tl;
    Gson gson;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    List<Partition> partitions;
    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        partitions = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = prefs.edit();
        tl = findViewById(R.id.tableLayoutList);
        gson = new Gson();

        if (prefs.contains("ListPartitions")) {
            json = prefs.getString("ListPartitions", "");
            partitions = gson.fromJson(json, type);
            for (Partition p : partitions) addRow(p);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Intent intent = getIntent();
        //if (intent != null) {
        //    String s = intent.getStringExtra(EXTRA_COMPONENT_NAME);
        //    ArrayList<Bitmap> L =  new ArrayList<>();
        //    Partition p = new Partition(s,L);
        //    addSco(p);
        //Toast toast = Toast.makeText(getApplicationContext(), "it works", Toast.LENGTH_LONG);
        // toast.show();
        // }
        FloatingActionButton fab = findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Accueil.this, AddScore.class);
                startActivity(intent);
            }
        });
    }

    public void addRow(final Partition p) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView tv = new EditText(this);
        tv.setText(p.nom);

        ImageButton remove = new ImageButton(this);
        remove.setImageResource(R.drawable.ic_delete);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Accueil.this);
                builder.setMessage("Supprimer ?")
                        .setTitle("")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                View row = (View) v.getParent();
                                // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
                                ViewGroup container = ((ViewGroup)row.getParent());
                                container.removeView(row);
                                container.invalidate();
                                partitions.remove(p);
                                json = gson.toJson(partitions);
                                prefsEditor.putString("ListPartitions",json);
                                prefsEditor.commit();
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL
                            }
                        })
                        .show();

            }
        });
        row.addView(tv);
        row.addView(remove);
        tl.addView(row);//, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
