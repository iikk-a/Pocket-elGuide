package com.example.pocketguide_honeywell;

import com.iikkag.pocketguide_honeywell.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class CaptureExceptionActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler("Android/data/com.iikkag.pocketguide_honeywell/crash_reports"));

        String nullString = null;
        System.out.println(nullString);
        setContentView(R.layout.activity_main);
    }
}
