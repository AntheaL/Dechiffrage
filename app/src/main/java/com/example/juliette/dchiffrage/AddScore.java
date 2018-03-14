package com.example.juliette.dchiffrage;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.HoughLines;
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
                L.add(new Page(targetUri.getPath(),new ArrayList<Rectangle>()));
                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                Bitmap btm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imageView.setImageBitmap(btm);
                layout.addView(imageView);
                photos.add(btm);
                Bitmap result  = detect(btm);

                ImageView linesView = new ImageView(this);
                linesView.setImageBitmap(result);
                layout.addView(linesView);

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
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap,src);
        Mat dst = new Mat();
        Mat cdst = new Mat();
        Canny(src, dst, 50, 200, 3);
        cvtColor(dst, cdst, COLOR_GRAY2BGR);
        Mat lines = new Mat();
        HoughLines(dst, lines, 1, Math.PI/180, 100, 0, 0);

        for(int i = 0; i < lines.cols(); i++ )
        {
            double data[] = lines.get(0,i);
            double rho = data[0];
            double theta = data[1];
            Point pt1 = new Point();
            Point pt2 = new Point();
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
            pt1.x = Math.round(x0 + 1000*(-b));
            pt1.y = Math.round(y0 + 1000*(a));
            pt2.x = Math.round(x0 - 1000*(-b));
            pt2.y = Math.round(y0 - 1000*(a));
            Imgproc.line( cdst, pt1, pt2, new Scalar(0,0,255), 3);
        }
        Bitmap result = Bitmap.createBitmap(lines.cols(), lines.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(cdst, result);
        return result;
    }
}