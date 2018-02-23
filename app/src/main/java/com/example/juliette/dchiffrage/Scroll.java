package com.example.juliette.dchiffrage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Scroll extends Fragment {
    private static String PARTITION;
    Partition p;
    Gson gson;
    Type type = new TypeToken<List<Partition>>() {
    }.getType();

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
    }

}