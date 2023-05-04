package com.example.eventsearch;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

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

    public static void snackbarValidationHelper(String text){
        Snackbar snackbar;
        snackbar = Snackbar.make(MainActivity.mainView, text, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#E1D9D1"));
        snackbar.setTextColor(Color.parseColor("#FF000000"));
        snackbar.show();
    }

    public static String getTwelveHoursTime(String hr24time) throws ParseException {
        // 24 hour format
        DateFormat twentyFourHourFormat = new SimpleDateFormat("HH:mm:ss");
        // 12 hour format
        DateFormat twelveHourFormat = new SimpleDateFormat("h:mm aa");
        Date date = twentyFourHourFormat.parse(hr24time);
        return twelveHourFormat.format(date);
    }

    public static String getAmericanDate(String normalTime) throws ParseException {
        // international format
        DateFormat normalFormat = new SimpleDateFormat("yyyy-MM-dd");
        // american format
        DateFormat americanFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = normalFormat.parse(normalTime);
        return americanFormat.format(date);
    }

    public static String getAmericanWordDate(String normalDate) throws ParseException {
        // international format
        DateFormat normalFormat = new SimpleDateFormat("yyyy-MM-dd");
        // american format
        DateFormat americanFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date date = normalFormat.parse(normalDate);
        return americanFormat.format(date);
    }

    public static String getFollowerProper(String s) {
        int digits = s.length();
        if (digits > 9) {
            return s.substring(0, digits - 9) + "B Followers";
        } else if (digits > 6) {
            return s.substring(0, digits - 6) + "M Followers";
        } else if (digits > 3) {
            return s.substring(0, digits - 3) + "K Followers";
        } else {
            return s + " Followers";
        }
    }
}
