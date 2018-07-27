package com.example.s216127904.codecentrix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecordPenalty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_penalty);
        DBAccess business = new DBAccess();


        business.GetTickets();

    }
}
