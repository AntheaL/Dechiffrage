package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Scroll extends Fragment {
    private static String PARTITION = "PARTITION";
    TranslateAnimation _translateAnimation;
    Partition p;
    Gson gson;
    String json;
    Type type = new TypeToken<Partition>() {}.getType();
    ImageView img;
    Bitmap x;
//    ObjectAnimator animator;
    int speed = 50;


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
        json = this.getArguments().getString(PARTITION);
        gson = new Gson();
        p = gson.fromJson(json, type);
        img = view.findViewById(R.id.img);
        x =p.getResult(getContext());
        img.setImageBitmap(x);
    }

    public void translate() {
        _translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, -img.getWidth(), TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
        int duration = (int) (40 * (img.getX() + img.getWidth()) / speed);
        _translateAnimation.setDuration(duration);
        _translateAnimation.setInterpolator(new LinearInterpolator());
        _translateAnimation.setFillAfter(true);
        _translateAnimation.setFillEnabled(true);
        img.startAnimation(_translateAnimation);
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
        _translateAnimation.cancel();
    }

}