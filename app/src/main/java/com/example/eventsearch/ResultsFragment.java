package com.example.eventsearch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultsFragment extends Fragment {
    private static final String TAG = "RootFragment";
    View resultView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resultView = inflater.inflate(R.layout.fragment_results, container, false);
        Utility.toastCheckHelper(resultView.getContext(),"Inside results!");
        Log.d("ResultFragment", "Inside Result Fragment");
        return resultView;
    }
}