package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScrollingActivity extends AppCompatActivity {
    ImageView imgRacer;
    ArrayList<CommentsModel> comments;
    DBAccess business;
    PenaltyModel penalty = new PenaltyModel();
    Button btnSave;
    Bitmap image;
    RadioButton rdBlue , rbYellow ,rdRed,rdTent1,rdTent2;
    EditText txtRacerNumber ,txtName;
    DownLoadPicture d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        business = new DBAccess();
        comments = business.GetComments();
        btnSave = findViewById(R.id.btnSave);
         rdBlue = findViewById(R.id.rdBlue);
         rbYellow = findViewById(R.id.rbYellow);
         rdRed = findViewById(R.id.rdRed);
        rdTent1 = findViewById(R.id.rdTent1);
        rdTent2 = findViewById(R.id.rdTent2);
        txtName = findViewById(R.id.txtRacerName);
        Button btnTakePic = findViewById(R.id.btnTakePic);
        imgRacer = findViewById(R.id.imgRacer);
        txtRacerNumber = findViewById(R.id.txtRacerNumber);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(showCamera,0);

            }
        });

        ImageView imgMap = findViewById(R.id.imgMap);

        d = new DownLoadPicture(null);
        imgMap.setImageBitmap(d.doInBackground());
        imgRacer.setImageBitmap(d.doInBackground());
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        ClearColor();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rdBlue:
                if (checked)
                    showDialog( view,3);
                    rdBlue.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case R.id.rbYellow:
                if (checked)
                    showDialog( view,2);
                rbYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
                break;
            case R.id.rdRed:
                if (checked)
                    showDialog( view,1);
                rdRed.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case R.id.rdTent1:
            if (checked)
                penalty.TentID = 1;
            break;
            case R.id.rdTent2:
                if (checked)
                    penalty.TentID = 2;///////////////////////////
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");
        imgRacer.setImageBitmap(image);
    }

    public void showDialog(View view,int ticketID)
    {
        penalty.TicketID = ticketID;/////////////////////////////


        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScrollingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.ticket_comment,null);

        final ListView list = mView.findViewById(R.id.lvComments);
        final CommentAdapter listAdapter = new CommentAdapter(getApplicationContext(),comments,ticketID);

        list.setAdapter(listAdapter);



        Button btnBack = mView.findViewById(R.id.btnBack);

        mBuilder.setView(mView);



        final AlertDialog dialog = mBuilder.create();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                penalty.CommentID =  listAdapter.getCommentID( position);//////////////////////////////////////////////////
                TextView t = findViewById(R.id.tvComment);
                t.setText(listAdapter.comments.get(position).CommentDescription);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void ToSavePenalty(View v)
    {

        String racerName = txtName.getText().toString();/////////////////



        String i = txtRacerNumber.getText().toString();
        try {
            penalty.RacerID = Integer.parseInt(i);////////////////////////////////////
        }catch (Exception e){
            e.printStackTrace();
        }


        penalty.RefID = 1;//from a file
        try{
            int z = image.getWidth();
            z++;
            penalty.sendImageToServer(image);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {

            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
          penalty.PenaltyTime = new Time(df.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                business.AddPenalty(penalty);
                try{
                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }});
        Looper.loop();
        t1.start();


        ClearColor();
        ClearText();

    }


    public class DownLoadPicture extends AsyncTask<Void,Void,Bitmap> {
        String name;
        public DownLoadPicture(String name)
        {
            if(name==null){
                name = "icon/map";
            }
            this.name = name;
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            String login_url = "http://sict-iis.nmmu.ac.za/codecentrix/MobileConnectionString/"+name.trim()+".png";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(1000 * 30);
                httpURLConnection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) httpURLConnection.getContent(),null,null);
            }catch (Exception e){
                e.printStackTrace();
                return  null;
            }

        }
    }
    public void ClearColor()
    {
        rdBlue.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        rbYellow.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        rdRed.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }
    public void ClearText()
    {
        txtName.setText("");
        txtRacerNumber.setText("");
        rdTent1.clearFocus();
        rdTent2.clearFocus();
        imgRacer.setImageBitmap(d.doInBackground());
    }
}