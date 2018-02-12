package com.example.juliette.dchiffrage;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.solver.widgets.Rectangle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

class MyAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    Partition p;

    public MyAdapter(Context context, Partition p) {
        mContext = context;
        p.this = p;
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

    @Override
    public Object instantiateItem(ViewGroup container, int pos_page, int position) {
        Page page = p.pages.get(pos_page);
        Rectangle rect = page.mesures.get(position);
        View itemView = mLayoutInflater.inflate(R.layout.layout, container, false);
        ImageView imageView = itemView.findViewById(R.id.image_mesure);
        imageView.setImageBitmap(Bitmap.createBitmap(page.btm,rect.x, rect.y,rect.width,rect.height));
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
