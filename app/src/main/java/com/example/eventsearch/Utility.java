package com.example.eventsearch;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

public class Utility {
    public static void toastCheckHelper(Context context, String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

//    private static View mainView = MainActivity.mainView;
//    private static View eventView = EventDetails.eventView;
    public static void snackbarHelper(String eventName, Boolean added, Boolean isMain){
        Snackbar snackbar;
        View view;
        if(isMain){
            view = MainActivity.mainView;
        }else {
            view = EventDetails.eventView;
        }
        if(added){
            snackbar = Snackbar.make(view, eventName+" added to favorites", Snackbar.LENGTH_LONG);
        }else{
            snackbar = Snackbar.make(view, eventName+" removed from favorites", Snackbar.LENGTH_LONG);
        }
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#E1D9D1"));
        snackbar.setTextColor(Color.parseColor("#FF000000"));
        snackbar.show();
    }
}
