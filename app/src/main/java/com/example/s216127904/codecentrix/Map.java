package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ViewModel.PenaltyModel;

public class Map extends AppCompatActivity {
    protected PhotoView imgMap;
    Handler h = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //code for back button
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        imgMap =(PhotoView) findViewById(R.id.imgMap);
        //DownLoadPicture downLoadPicture = new DownLoadPicture(null);
        //imgMap.setImageBitmap(downLoadPicture.doInBackground());
        helpThread r = new helpThread("");
        new Thread(r).start();
        imgMap.setAdjustViewBounds(true);
    }
    public class DownLoadPicture extends AsyncTask<Void, Void, Bitmap> {
        String name;
        public DownLoadPicture(String name) {
            if (name == null || name.equals("")) {
                name = "icon/map";
            }
            this.name = name;
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            String login_url = "http://sict-iis.nmmu.ac.za/codecentrix/MobileConnectionString/" + name.trim() + ".png";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(1000 * 30);
                httpURLConnection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) httpURLConnection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            finish();
            return super.onOptionsItemSelected(item);
    }
    class helpThread implements Runnable {
        String name;
        Bitmap image = null;
        public helpThread(String name) {
            if (name == null) {
                name = "icon/map";
            }
            this.name = name;
        }


        @Override
        public void run() {


            String login_url = "http://sict-iis.nmmu.ac.za/codecentrix/MobileConnectionString/icon/map.png";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(1000 * 30);
                httpURLConnection.setReadTimeout(1000 * 30);

                image = (BitmapFactory.decodeStream((InputStream) httpURLConnection.getContent(), null, null));
            } catch (Exception e) {
                e.printStackTrace();

            }
            h.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        if(image!=null)
                        imgMap.setImageBitmap(image);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            });

        }
    }
}
