package com.example.s216127904.codecentrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UploadImage extends AsyncTask<Void,Void, Void> {
    Bitmap image;
    String name;

    public  UploadImage(Bitmap image, String name){
        this.image = image;
        this.name = name;
    }

    @Override
    protected Void doInBackground(Void...params){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        ArrayList<NameValuePair> dataToSend = new ArrayList<>();
        dataToSend.add( new BasicNameValuePair("image",encodedImage));
        dataToSend.add(new BasicNameValuePair("name",name ));

        HttpParams httpRequestParams = getHttpRequestParams();
        HttpClient client = new DefaultHttpClient(httpRequestParams);
        HttpPost post = new HttpPost("http://sict-iis.nmmu.ac.za/codecentrix/IronMan/" + "SavePicture.php");
        try {
             post.setEntity(new UrlEncodedFormEntity(dataToSend));
             client.execute(post);
        }catch (Exception a ){
            a.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        //Set results here
    }

    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
        return httpRequestParams;
    }
}


