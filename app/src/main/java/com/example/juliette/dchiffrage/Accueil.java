package com.example.juliette.dchiffrage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Accueil extends AppCompatActivity {

    Type type = new TypeToken<List<Partition>>(){}.getType(); // utilisé pour la désérialisation
    TableLayout tl;
    SharedPreferences prefs; // là où la liste des partitions est stockée
    SharedPreferences.Editor prefsEditor;
    List<Partition> partitions;
    Gson gson; // permet d'accomplir la sérialisation
    String json; // résultat de la sérialisation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        partitions = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = prefs.edit();
        tl = findViewById(R.id.tableLayoutList);
        gson = new Gson();

        if (prefs.contains("ListPartitions")) { // Si la liste de parttions n'est pas vide
            json = prefs.getString("ListPartitions", "");
            partitions = gson.fromJson(json, type); // On désérialise cette liste
            for (Partition p : partitions) addRow(p);
        }

        // permet le passage à l'activité AddScore
        FloatingActionButton fab = findViewById(R.id.home_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Accueil.this, AddScore.class);
                startActivity(intent);
            }
        });
    }

    // ajoute une ligne dans le TableLayout
    public void addRow(final Partition p) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        Button btn = new Button(this); // permet le assage à l'activité Jeu
        btn.setText(p.nom);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2 = new Intent(Accueil.this, Jeu.class);
                intent_2.putExtra("Partition", String.valueOf(partitions.indexOf(p)));
                Accueil.this.startActivity(intent_2);
            }
        });

        ImageButton remove = new ImageButton(this); // supprime l'image du TableLayout et des préférences
        remove.setImageResource(R.drawable.ic_delete);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Accueil.this);
                builder.setMessage("Supprimer ?")
                        .setTitle("")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                View row = (View) v.getParent();
                                // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
                                ViewGroup container = ((ViewGroup) row.getParent());
                                container.removeView(row);
                                container.invalidate();
                                partitions.remove(p);
                                json = gson.toJson(partitions);
                                prefsEditor.putString("ListPartitions", json);
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
        row.addView(btn);
        row.addView(remove);
        tl.addView(row);
    }

    // Pas utilisé pour le moment
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
        return true;
    }

}
