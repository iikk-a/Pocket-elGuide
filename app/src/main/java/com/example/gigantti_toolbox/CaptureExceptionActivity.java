package com.example.gigantti_toolbox;

import com.iikkag.gigantti_toolbox.R;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CaptureExceptionActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new CustomizedExceptionHandler("Android/data/com.iikkag.gigantti_toolbox/crash_reports"));

        String nullString = null;
        System.out.println(nullString);
        setContentView(R.layout.activity_main);
    }
}
