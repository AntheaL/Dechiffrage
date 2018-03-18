package com.example.juliette.dchiffrage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Partition implements Serializable {
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
            for (Rect rect : page.mesures) {
                l += rect.right-rect.left;
                max_height = Math.max(max_height, rect.bottom-rect.top);
            }
        }
        return new int[] {l, max_height};
    }

    public ArrayList<Bitmap> combine(Context context) {
        ArrayList<Bitmap> L = new ArrayList<>();
        try {
            Bitmap btm;
            for (Page page : pages) {
                btm = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(page.path)));
                for (Rect rect : page.mesures)
                    L.add(Bitmap.createBitmap(btm, rect.left, rect.top, rect.left - rect.right, rect.top - rect.bottom));
            }
            return L;
        }
        catch (IOException e) {
            e.printStackTrace();
            return L;
        }
    }


    public Bitmap getResult(Context context) {
        int[] t = this.sizes();
        int position = 0;
        Bitmap result = Bitmap.createBitmap(t[0],t[1],Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        ArrayList<Bitmap> L = combine(context);
        for(Bitmap btm : L) {
            canvas.drawBitmap(btm,position, 0, paint);
            position += btm.getWidth();
        }
        return result;
    }
}
