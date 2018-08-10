package ViewModel;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.s216127904.codecentrix.UploadImage;

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
    public Double longitude;
    public Double latitude;

    public  void sendImageToServer(Bitmap imageToUpload) throws IOException {
        //Quickly give the predicted path

        PenaltyPicturePath = "http://sict-iis.nmmu.ac.za/codecentrix/IronMan/pictures/"+ "PIC"+ new SimpleDateFormat("yyyyMMddHHmmss'.PNG'").format(new Date());
        String uploadImageName= "PIC"+ new SimpleDateFormat("yyyyMMddHHmmss'.PNG'").format(new Date());
        //now send the actual image to the database...
        Bitmap image = imageToUpload;
        new UploadImage(image, uploadImageName).execute();
    }
    public  void ClearPenalty()  {
         CommentID = 0;
         TentID = 0;
         RacerID = 0;
         TicketID = 0;
         PenaltyPicturePath  = null;
         longitude = null;
         latitude = null;
    }

}
