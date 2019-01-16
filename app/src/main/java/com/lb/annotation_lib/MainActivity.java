package com.lb.annotation_lib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lib.processor.annotation.CalendarContract;
import com.lib.processor.annotation.SharePreferences;

@SharePreferences(preferencesName = "share")
@CalendarContract(calendarName = "test",accountName = "test")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
