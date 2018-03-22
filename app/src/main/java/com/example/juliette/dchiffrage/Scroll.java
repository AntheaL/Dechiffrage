package com.example.juliette.dchiffrage;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Scroll extends Fragment {
    private static String PARTITION = "PARTITION";
    TranslateAnimation _translateAnimation;
    Partition p;
    Gson gson;
    String json;
    Type type = new TypeToken<Partition>() {}.getType();
    HorizontalScrollView scrollView;
    LinearLayout linear;
    AnimatorSet animators = new AnimatorSet();
    ImageView img;
    float facteur;
    Bitmap x;
//    ObjectAnimator animator;
    int speed = 50;
    private ArrayList<Rect> List;


    public static Scroll newInstance(String json) {
        Scroll fragment = new Scroll();
        Bundle args = new Bundle();
        args.putString(PARTITION, json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scroll, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        float scalingFactor = 4f;
        json = this.getArguments().getString(PARTITION);
        gson = new Gson();
        p = gson.fromJson(json, type);
        scrollView = view.findViewById(R.id.horizontalScrollView1);
        linear = view.findViewById(R.id.linear);
        ArrayList<Bitmap> L=p.combine(getContext());
        for(Bitmap x:L) {
            img = new ImageView(getActivity());
            linear.addView(img);
            img.setImageBitmap(x);
//            img.setScaleX(scalingFactor);
//            img.setScaleY(scalingFactor);
//            linear.setScaleX(scalingFactor);
//            linear.setScaleY(scalingFactor);
//            scrollView.setScaleX(scalingFactor);
//            scrollView.setScaleY(scalingFactor);

//            img.getLayoutParams().height=500; // A corriger tout ca
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Les images sont correctement stretchees
            img.setAdjustViewBounds(true);
        }
    }


    public void translate() {
        int xmax=0;
        List=p.pages.get(0).mesures;
        for(int k=0;k<List.size();k++) xmax+=List.get(k).right;
        int x0=scrollView.getScrollX();
        ObjectAnimator xTranslate = ObjectAnimator.ofInt(scrollView,"scrollX",x0,xmax);
        // J'imagine il faut 4 beats par mesure. Donc connaissant le nombre de mesures n, on doit mettre 4*n battements a speed bpm, donc speed/60/1000 battements par ms
        // Donc il faut 4*60*1000*n/speed ms
        float n=(xmax-x0)/(List.get(0).right-List.get(0).left); //Code approximatif a corriger
        animators.setDuration(Math.round(4*60*1000*n/speed));
        xTranslate.setInterpolator(new LinearInterpolator());
        animators.play(xTranslate);
        animators.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                int currentx=scrollView.getScrollX();
                scrollView.scrollTo(currentx,0);

                // TODO Auto-generated method stub

            }
        });
        animators.start();

    }
    /* public void translate() {
        animator = ObjectAnimator.ofFloat(img,"translationX", img.getX(), -img.getWidth());
        int duration =(int)(40*(img.getX()+img.getWidth())/speed);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                ((Jeu) getActivity()).changeBackground();

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.start();
    } */


    public void stopTranslate() {
        animators.cancel();
    }
    public void resume() {
        animators.resume();
    }

    public void goTo(int x) {
        scrollView.scrollTo(x,0);
    }

}