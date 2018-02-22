package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.constraint.solver.widgets.Rectangle;

import java.util.ArrayList;

public class Partition {
    String nom;
    ArrayList<Page> pages;
    public Partition(String m, ArrayList<Page> L) {
        nom = m;
        pages = L ;
    }
    public int nbMesures() {
        int nb = 0;
        for(Page page:pages) nb += page.mesures.size();
        return nb;
    }

    public int[] sizes() {
        int l = 0;
        int max_height = 0;
        for(Page page: pages) {
            for (Rectangle rect : page.mesures) {
                l += rect.width;
                max_height = Math.max(max_height, rect.height);
            }
        }
        return new int[] {l, max_height}
    }

    public Bitmap combine() {
        int[] t = sizes();
        Bitmap result = Bitmap.createBitmap(t[0],t[1],Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        Bitmap btm;
        int position = 0;
        for (Page page:pages) {
            btm = BitmapFactory.decodeFile(page.path);
            for(Rectangle rect: page.mesures) {
                canvas.drawBitmap(Bitmap.createBitmap(btm,rect.x, rect.y,rect.width,rect.height),position, 0, paint);
            }
        return result;
        }
    }
}
