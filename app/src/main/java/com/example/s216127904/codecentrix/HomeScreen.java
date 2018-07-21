package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Observable;

public class HomeScreen extends AppCompatActivity {
    ProgressBar loadBar;
    ImageView imLogo;
    int progressStatus =0;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        imLogo = findViewById(R.id.imLogo);
        loadBar = findViewById(R.id.progressBar4);
        Animation slideOut =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_out);
        slideOut.setDuration(5000);
        imLogo.setAnimation(slideOut);
        Timer(3000);
    }
    public void Timer(int duration){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 3000){
                    // Update the progress status
                    progressStatus +=1;
                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(30);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadBar.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent groupDetails = new Intent(getApplicationContext(), GroupDetails.class);
                startActivity(groupDetails);
                finish();
            }
        },duration);
    }
    public void ToGroupDetails(View view){
      // Timer(100);
    }

}
