package com.example.eventsearch;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    public static void toastCheckHelper(Context context, String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
