package com.example.juliette.dchiffrage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.cvtColor;

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
    static final int REQUEST_IMAGE_CAPTURE = 1;
    double portee; // hauteur d'une portée
    double blanc; // distance entre deux portées

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

        FloatingActionButton fab_camera = findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap rslt = (Bitmap) extras.get("data");
        }
        if (resultCode == RESULT_OK) {
            try {
                Uri targetUri = data.getData();
                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                Bitmap btm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imageView.setImageBitmap(btm);
                layout.addView(imageView);
                photos.add(btm);
                Mat lines = detect2(btm);
                ArrayList<Double> P =  search(lines); // cherche les coordonnées verticales des portées
                portee = P.get(1)-P.get(0);
                blanc = P.get(2)-P.get(1);
                ArrayList<Rect> rectangles = new ArrayList<>(); // un Rect = une ligne entière
                for(int i = 0; i<L.size(); i+=2) {
                    rectangles.add(new Rect(0, (int)(P.get(i)-blanc/2), btm.getWidth(),(int)(P.get(i)+blanc/2))); // left, top, right, bottom
                }
                L.add(new Page(targetUri.getPath(),rectangles));

                // Bitmap result  = detect(btm);
                // ImageView linesView = new ImageView(this);
                //linesView.setImageBitmap(result);
                // layout.addView(linesView);

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

    public static Bitmap detect(Bitmap bitmap) {
        OpenCVLoader.initDebug();
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap,src);
        Mat dst = new Mat();
        Mat cdst = new Mat();
        Mat bwsrc = new Mat();
        cvtColor(src, bwsrc, COLOR_BGR2GRAY);
        Canny(bwsrc, dst, 100, 700, 3);
        cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(dst, lines, 5, Math.PI/180, 200, 300, 20);

        for (int x = 0; x < lines.rows(); x++)
        {
            double[] vec = lines.get(x,0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(cdst, start, end, new Scalar(255,0,0), 5);

        }

        Bitmap result = Bitmap.createBitmap(cdst.cols(), cdst.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cdst, result);

        return result;
    }

    public static Mat detect2(Bitmap bitmap) {
        OpenCVLoader.initDebug();
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat dst = new Mat();
        Mat cdst = new Mat();
        Mat bwsrc = new Mat();
        cvtColor(src, bwsrc, COLOR_BGR2GRAY);
        Canny(bwsrc, dst, 100, 700, 3);
        cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(dst, lines, 5, Math.PI / 180, 200, 300, 20);
        return lines;
    }

        public static ArrayList<Double> search(Mat lines) {
        ArrayList<Double> pos = new ArrayList<>();
        for(int x=0; x<lines.rows(); x++) {
            pos.add(lines.get(x,0)[1]);
        }
        sort(pos);
        ArrayList<Double> L = new ArrayList<>();
        double y = pos.get(0);
        L.add(y);
        double z;
        for(int i = 0; i<pos.size(); i++) {
            z = pos.get(i);
            if(Math.abs(z-y)>170) {
                L.add(pos.get(i-1)); // ajout position verticale de la ligne en bas de portée
                L.add(z); // ajoute position verticale de la première ligne portée suivante
                y=z;
            }
        }
        return L;
    }
}