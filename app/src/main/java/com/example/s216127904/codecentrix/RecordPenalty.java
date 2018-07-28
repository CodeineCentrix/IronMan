package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class RecordPenalty extends AppCompatActivity {
    ImageView imgRacer;
    ArrayList<CommentsModel> comments;
    DBAccess business;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_penalty);
        business = new DBAccess();
        comments = business.GetComments();

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
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(RecordPenalty.this);
        View mView = getLayoutInflater().inflate(R.layout.ticket_comment,null);

       final ListView list = mView.findViewById(R.id.lvComments);
      final ListAdapter listAdapter = new CommentAdapter(getApplicationContext(),comments,ticketID);

      list.setAdapter(listAdapter);

      list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

          }
      });

      Button btnBack = mView.findViewById(R.id.btnBack);

        mBuilder.setView(mView);



        final AlertDialog dialog = mBuilder.create();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
