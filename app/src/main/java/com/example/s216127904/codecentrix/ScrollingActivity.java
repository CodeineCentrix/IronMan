package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        business = new DBAccess();
        comments = business.GetComments();
        btnSave = findViewById(R.id.btnSave);
        RadioButton rdBlue = findViewById(R.id.rdBlue);
        RadioButton rbYellow = findViewById(R.id.rbYellow);
        RadioButton rdRed = findViewById(R.id.rdRed);
        Button btnTakePic = findViewById(R.id.btnTakePic);
        imgRacer = findViewById(R.id.imgRacer);

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(showCamera,0);

            }
        });



    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rdBlue:
                if (checked)
                    showDialog( view,3);
                break;
            case R.id.rbYellow:
                if (checked)
                    showDialog( view,2);
                break;
            case R.id.rdRed:
                if (checked)
                    showDialog( view,1);
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
        Bitmap image = (Bitmap) data.getExtras().get("data");
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
        EditText txtName = findViewById(R.id.txtRacerName);
        String racerName = txtName.getText().toString();/////////////////

        EditText txtRacerNumber = findViewById(R.id.txtRacerNumber);

        String i = txtRacerNumber.getText().toString();
        try {
            penalty.RacerID = Integer.parseInt(i);////////////////////////////////////
        }catch (Exception e){
            e.printStackTrace();
        }


        penalty.RefID = 1;//from a file
        penalty.PenaltyPicturePath = "http://sict-iis.nmmu.ac.za/codecentrix/Ironman/pictures/";
        try {

            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
          penalty.PenaltyTime = new Time(df.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        business.AddPenalty(penalty);
    }
}