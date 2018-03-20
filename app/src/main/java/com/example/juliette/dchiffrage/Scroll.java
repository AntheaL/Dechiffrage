package com.example.juliette.dchiffrage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        Bitmap x =p.getResult(getContext());
        img.setImageBitmap(x);
    }

    /* public void startTranslate() {
        _translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, -300f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
        _translateAnimation.setDuration(8000);
        _translateAnimation.setRepeatCount(-1);
        _translateAnimation.setRepeatMode(Animation.RESTART);
        _translateAnimation.setInterpolator(new LinearInterpolator());
        img.startAnimation(_translateAnimation);
    } */

}