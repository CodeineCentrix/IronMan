package com.example.s216127904.codecentrix;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PenaltyModel {
    private int CommentID;
    private int TentID;
    private int RacerID;
    private int RefID;
    private int TicketID;
    private String PenaltyPicturePath ;
    private String PenaltyLocation ;
    private Date PenaltyDate;
    private Time PenaltyTime;


    public  void sendImageToServer(Bitmap imageToUpload) throws IOException {
        //Quickly give the predicted path

        PenaltyPicturePath = "http://sict-iis.nmmu.ac.za/codecentrix/IronMan/pictures/"+ "PIC"+ new SimpleDateFormat("yyyyMMddHHmmss'.PNG'").format(new Date());
        String uploadImageName= "";
        //now send the actual image to the database...
        Bitmap image = imageToUpload;
        new UploadImage(image, uploadImageName).execute();
    }

}
