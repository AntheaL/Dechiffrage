package com.example.juliette.dchiffrage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;

class MyAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    Partition p;
    ArrayList<Bitmap> L;

    public MyAdapter(Context context, Partition p) {
        mContext = context;
        this.p = p;
        L = p.combine();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return p.nbMesures();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    public Object instantiateItem(ViewGroup container, int pos) {
        View itemView = mLayoutInflater.inflate(R.layout.layout, container, false);
        ImageView imageView = itemView.findViewById(R.id.image_mesure);
        imageView.setImageBitmap(L.get(pos));
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
