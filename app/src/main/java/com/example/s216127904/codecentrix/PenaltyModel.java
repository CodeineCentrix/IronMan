package com.example.s216127904.codecentrix;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PenaltyModel {
    public int CommentID;
    public int TentID;
    public int RacerID;
    public int RefID;
    public int TicketID;
    public String PenaltyPicturePath ;
    public String PenaltyLocation ;
    public Date PenaltyDate;
    public Time PenaltyTime;


    public  void sendImageToServer(Bitmap imageToUpload) throws IOException {
        //Quickly give the predicted path

        PenaltyPicturePath = "http://sict-iis.nmmu.ac.za/codecentrix/IronMan/pictures/"+ "PIC"+ new SimpleDateFormat("yyyyMMddHHmmss'.PNG'").format(new Date());
        String uploadImageName= "";
        //now send the actual image to the database...
        Bitmap image = imageToUpload;
        new UploadImage(image, uploadImageName).execute();
    }

}
