package com.example.juliette.dchiffrage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Scroll extends Fragment {
    private static String PARTITION;
    TranslateAnimation _translateAnimation;
    Partition p;
    Gson gson;
    Type type = new TypeToken<List<Partition>>() {}.getType();
    ImageView img;

    public static Scroll newInstance(String json) {
        Scroll fragment = new Scroll();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString(PARTITION, json);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scroll, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        p = gson.fromJson(PARTITION, type);
        gson = new Gson();
        img = view.findViewById(R.id.img);
        img.setImageBitmap(p.getResult());

    }

    public void startTranslate() {
        _translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, -300f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
        _translateAnimation.setDuration(8000);
        _translateAnimation.setRepeatCount(-1);
        _translateAnimation.setRepeatMode(Animation.RESTART);
        _translateAnimation.setInterpolator(new LinearInterpolator());
        img.startAnimation(_translateAnimation);
    }

}