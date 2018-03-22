package com.example.juliette.dchiffrage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


// Fragment non utilisé pour l'instant.
// Permet de gérer les swipes une fois les mesures reconnues.

public class Swipe extends Fragment implements View.OnTouchListener {
    ViewPager mViewPager;
    MyAdapter adapter;
    Partition p;
    Gson gson;
    String json;
    Type type = new TypeToken<List<Partition>>(){}.getType();

    public static Swipe newInstance(String json) {
        Swipe fragment = new Swipe();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putString("PARTITION",json);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        json = this.getArguments().getString("PARTITION");
        return inflater.inflate(R.layout.fragment_swipe, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        p = gson.fromJson(json, type);
        adapter = new MyAdapter(view.getContext(), p);
        mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        gson = new Gson();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_SCROLL:

                break;
        }
        return false;
    }
}