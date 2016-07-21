package com.example.neelmani.fukrey.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.neelmani.fukrey.R;

/**
 * Created by Neelmani on 15-Nov-15.
 */
public class FavouritesUpdateFragment extends Fragment {

    public FavouritesUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite_update, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
     }

}