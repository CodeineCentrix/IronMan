package com.example.s216127904.codecentrix;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class GroupDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        try {
            getSupportActionBar().setTitle("Group Details");
            animate_cards(this);
        }catch ( Exception a  ){

        }

    }

    public void animate_cards( final Context a){
        new Thread(new Runnable() {
            public void run() {

                Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation( a , R.anim.anim);
                CardView crd1 = (CardView) findViewById(R.id.crdHaich);
                crd1.startAnimation(hyperspaceJumpAnimation);
                Animation hyperspaceJumpAnimation2 = AnimationUtils.loadAnimation( a, R.anim.anim);
                CardView crd2 = (CardView) findViewById(R.id.crdAnathi);
                crd2.startAnimation(hyperspaceJumpAnimation2);
            }
        }).start();
    }
}
