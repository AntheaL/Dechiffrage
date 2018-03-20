package com.example.juliette.dchiffrage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    Uri target;
    String mCurrentPhotoPath;
    Type type = new TypeToken<List<Partition>>(){}.getType();
    int distance;// motié de la hauteur d'une portée une fois isolée
    int min_x; // extrémité gauche de la portée
    int max_x; // extrémité droite de la portée

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
                Partition p = new Partition(name.getText().toString(),L,distance*2);
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

        FloatingActionButton fab = findViewById(R.id.fab);
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
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(AddScore.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        target=photoURI;
                        startActivityForResult(takePictureIntent, 1);
                    }
                }

            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap btm = null;
            try {
                if (requestCode == 1) {
                    btm = BitmapFactory.decodeStream(getContentResolver().openInputStream(target));
                }
                if (requestCode == 0) {
                    target = data.getData();
                    btm = BitmapFactory.decodeStream(getContentResolver().openInputStream(target));
                    try{
                        File file = createImageFile();
                        mCurrentPhotoPath=FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider", file).toString();
                        OutputStream stream = null;
                        stream = new FileOutputStream(file);
                        btm.compress(Bitmap.CompressFormat.JPEG,100,stream); // Enregistre l'image comme jpeg
                        stream.flush();
                        stream.close();
                    }catch (IOException e){e.printStackTrace();}
                 }
                ImageView imageView = new ImageView(this);
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(btm);
                layout.addView(imageView);
                photos.add(btm);
//                Bitmap tst = detect(btm);
                Mat lines = detect2(btm);
                ArrayList<Double> P = this.search(lines); // cherche les coordonnées verticales des portées
                double portee = P.get(1) - P.get(0);
                double blanc = P.get(2) - P.get(1);
                distance = (int) (portee+blanc)/2;
                ArrayList<Rect> rectangles = new ArrayList<>();// un Rect = une ligne entière
                for(int i = 0; i<P.size(); i+=2) {
                    rectangles.add(new Rect(min_x, (int)(P.get(i)-distance), max_x,(int)(P.get(i)+distance))); // left, top, right, bottom
                }
                // rectangles.add(new Rect(0, 0, btm.getWidth(), btm.getHeight()));
                L.add(new Page(new String(mCurrentPhotoPath), rectangles));

                // Bitmap result  = detect(btm);
                // ImageView linesView = new ImageView(this);
                //linesView.setImageBitmap(result);
                // layout.addView(linesView);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                Toast errorToast = Toast.makeText(this.getApplicationContext(), "Couldn't recognize the measures", Toast.LENGTH_SHORT);
                errorToast.show();
            }
        }
    }

    public void save(Partition p) {
        partitions.add(p);
        json = gson.toJson(partitions);
        prefsEditor.putString("ListPartitions",json);
        prefsEditor.commit();
    }

    public static Bitmap detect(Bitmap bitmap) { // dessine les lignes sur l'image d'origine
        OpenCVLoader.initDebug();
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap,src);
        Mat dst = new Mat();
        Mat cdst = new Mat();
        Mat bwsrc = new Mat();
        cvtColor(src, bwsrc, COLOR_BGR2GRAY);
        Canny(bwsrc,dst,200,800,3,false);
        // threshold(bwsrc,dst,200,255,THRESH_BINARY);
        // Sobel(bwsrc, dst,bwsrc.depth(), 0, 1);
        cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        Mat lines = new Mat();
//        Imgproc.HoughLinesP(dst, lines, 5, Math.PI/180, 200, 500, 100);

//        for (int x = 0; x < lines.rows(); x++)
//        {
//            double[] vec = lines.get(x,0);
//            double x1 = vec[0],
//                    y1 = vec[1],
//                    x2 = vec[2],
//                    y2 = vec[3];
//            Point start = new Point(x1, y1);
//            Point end = new Point(x2, y2);
//
//            Imgproc.line(cdst, start, end, new Scalar(255,0,0), 5);
//
//        }

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
        Imgproc.HoughLinesP(dst, lines, 5, Math.PI / 180, 250, 200, 20);
        return lines;
    }

    public ArrayList<Double> search(Mat lines) {
        ArrayList<Double> pos = new ArrayList<>();
        double[] l;
        for(int x=0; x<lines.rows(); x++) {
            l = lines.get(x,0);
            if(Math.abs(l[2]-l[0])>Math.abs((l[3]-l[1]))) {
                min_x = Math.min(min_x, (int) l[0]);
                max_x = Math.max(max_x, (int) l[2]);
                pos.add(l[1]);
            }
        }
        sort(pos);
        ArrayList<Double> L = new ArrayList<>();
        try {
            double y = pos.get(0);
            L.add(y);
            double z;
            for(int i = 0; i<pos.size(); i++) {
                z = pos.get(i);
                if (Math.abs(z - y) > 170) {
                    L.add(pos.get(i - 1)); // ajout position verticale de la ligne en bas de portée
                    L.add(z); // ajoute position verticale de la première ligne portée suivante
                    y = z;
                }
            }
        }
        catch(IndexOutOfBoundsException e) {
            Toast errorToast = Toast.makeText(this.getApplicationContext(), "Couldn't recognize the measures", Toast.LENGTH_SHORT);
            errorToast.show();
        }
        return L;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
}