package com.example.s216127904.codecentrix;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import de.hdodenhof.circleimageview.CircleImageView;

public class GroupDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        CircleImageView img1 = findViewById(R.id.person_photo);
        CircleImageView im2 = findViewById(R.id.anathi_photo);
        ScalingImage scaler = new ScalingImage();
        img1.setImageBitmap(scaler.ScaleImg(R.drawable.prayerroom,getResources()));
        im2.setImageBitmap(scaler.ScaleImg(R.drawable.anathiwithpcs,getResources()));

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
