package com.example.s216127904.codecentrix;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralMethods {
    Context context;

    public GeneralMethods(Context c)
    {
        context= c;
    }
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if ( intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity( intent);
        }
    }
    public void writeToFile(String data,String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFromFile(String fileName) {
        String ret = "";
        try {if(context != null){
            InputStream inputStream = context.openFileInput(fileName);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }}
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return ret;
    }
    public String[] Read(String fileName,String splitter){
        return readFromFile(fileName).split(splitter);
    }
    public String GetDate(){
        SimpleDateFormat df = new SimpleDateFormat("MMM dd");
        Date date = new Date();
        return df.format(date.getTime());
    }
    public Bitmap ScaleImg(int pic, Resources res){
        Bitmap scaledImg;
        BitmapFactory.Options op = new BitmapFactory.Options();

        op.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,pic,op);



        op.inJustDecodeBounds = false;
        scaledImg = BitmapFactory.decodeResource(res,pic,op);

        return scaledImg;
    }
    public Bitmap ScaleImg(int pic, byte[] res){
        Bitmap scaledImg;
        BitmapFactory.Options op = new BitmapFactory.Options();

        op.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res,0,res.length,op);

        int imgWidth = op.outWidth;
        if(imgWidth>1500){
            op.inSampleSize = 20;
        }else if(imgWidth>500){
            op.inSampleSize = 5;
        }else if(imgWidth>400){
            op.inSampleSize = 4;
        }else if(imgWidth>300){
            op.inSampleSize = 3;
        }else{
            op.inSampleSize = 2;
        }
        op.inJustDecodeBounds = false;
        scaledImg = BitmapFactory.decodeByteArray(res,0,res.length,op);

        return scaledImg;
    }

    public  boolean isLocationServicesAvailable() {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        return isAvailable ;
    }
}