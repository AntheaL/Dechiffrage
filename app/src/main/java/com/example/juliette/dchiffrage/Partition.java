package com.example.juliette.dchiffrage;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Partition implements Serializable {
    String nom;
    ArrayList<Page> pages;
    int hauteur; // hauteur d'une portée une fois isolée

    public Partition(String m, ArrayList<Page> L, int h) {
        nom = m;
        pages = L ;
        hauteur = h;
    }
    public int nbMesures() {
        int nb = 0;
        for(Page page:pages) nb += page.mesures.size();
        return nb;
    }

    // renvoie la somme des tailles horizontales des portées
    public int size() {
        int l = 0;
        // int max_height = 0;
        for(Page page: pages) {
            for (Rect rect : page.mesures) {
                l += rect.right-rect.left;
                // max_height = Math.max(max_height, rect.bottom-rect.top);
            }
        }
        return l;
    }

    // renvoie la "position horizontale" de chaque portée si on les concatène
    public ArrayList<Integer> getPos() { ArrayList<Integer> L = new ArrayList<>();
        int pos = 0;
        for(Page page: pages) {
            for (Rect rect : page.mesures) {
                L.add(pos);
                pos += rect.right-rect.left;
            }
        }
        return L;
    }

    // Renvoie la liste des portées
    public ArrayList<Bitmap> combine(Context context) {
        ArrayList<Bitmap> L = new ArrayList<>();
        try {
            Bitmap btm;
            ContentResolver solver = context.getContentResolver();
            for (Page page : pages) {
                Uri path = Uri.parse(page.path);
//                InputStream str = solver.openInputStream(path);
//                btm = BitmapFactory.decodeStream(str);
                btm = MediaStore.Images.Media.getBitmap(solver,path);
//                str.close();
                for (Rect rect : page.mesures)
                    L.add(Bitmap.createBitmap(btm, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top));

            }
            return L;
        }
        catch (IOException e) {
            e.printStackTrace();
            return L;
        }
    }


    // Rnevoie une très longue image où toutes les portées sont concaténées
    // Trop longue, d'ailleurs. Finalement, cette méthode n'est pas utilisée.
    // Les portées sont simplements disposées l'une derrière l'autre dans la ScrollView de la classe Scroll.
    public Bitmap getResult(Context context) {
        int l = this.size();
        int position = 0;
        Bitmap result = Bitmap.createBitmap(l,hauteur,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        ArrayList<Bitmap> L = combine(context);
        for(Bitmap btm : L) {
            canvas.drawBitmap(btm,position, 0, null);
            position += btm.getWidth();
        }
        return result;
    }
}
