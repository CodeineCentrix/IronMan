package com.example.s216127904.codecentrix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ScrollingActivity extends AppCompatActivity {
    ImageView imgRacer;
    ArrayList<CommentsModel> comments;
    DBAccess business;
    PenaltyModel penalty = new PenaltyModel();
    Button btnSave, btnOk, btnTakePic;
    Bitmap image;
    RadioButton rdBlue, rbYellow, rdRed, rdTent1, rdTent2;
    EditText txtRacerNumber, txtName;
    DownLoadPicture d;
    LocationManager locationManager;
    LocationListener locationListener;
    FusedLocationProviderClient client;
    GeneralMethods m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        business = new DBAccess();
        m = new GeneralMethods(getApplicationContext());
        RequestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        client.getLastLocation().addOnSuccessListener(ScrollingActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(m.isLocationServicesAvailable()) {
                    penalty.longitude = location.getLongitude();
                    penalty.latitude = location.getAltitude();
                }
            }
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SetUpControlers();
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(showCamera, 10);

            }
        });
        d = new DownLoadPicture(null);
        imgRacer.setImageBitmap(d.doInBackground());

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }
    public void RequestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        ClearColor();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdBlue:
                if (checked)
                    showDialog(view, 3);
                rdBlue.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case R.id.rbYellow:
                if (checked)
                    showDialog(view, 2);
                rbYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
                break;
            case R.id.rdRed:
                if (checked)
                    showDialog(view, 1);
                rdRed.setBackgroundColor(getResources().getColor(R.color.red));
                break;
        }
    }
    public void onTentClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
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
        if(requestCode==10 && data!=null){
            image = (Bitmap) data.getExtras().get("data");
            imgRacer.setImageBitmap(image);
        }

    }
    public void showDialog(View view, int ticketID) {
        penalty.TicketID = ticketID;/////////////////////////////
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScrollingActivity.this);
        View commentView = getLayoutInflater().inflate(R.layout.ticket_comment, null);
        final ListView list = commentView.findViewById(R.id.lvComments);
        final CommentAdapter listAdapter = new CommentAdapter(getApplicationContext(), comments, ticketID);
        list.setAdapter(listAdapter);
        Button btnBack = commentView.findViewById(R.id.btnBack);
        mBuilder.setView(commentView);
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
                penalty.CommentID = listAdapter.getCommentID(position);//////////////////////////////////////////////////
                TextView t = findViewById(R.id.tvComment);
                t.setText(listAdapter.comments.get(position).CommentDescription);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void ToSavePenalty(View v) {

        String i = txtRacerNumber.getText().toString();
        int refid;
        try {
             refid = Integer.parseInt(m.Read("user.txt", ",")[0]);
        }catch (Exception e){refid=1;}
        try{
            penalty.RefID = refid;
            penalty.RacerID = Integer.parseInt(i);////////////////////////////////////
            if(image!=null)
                penalty.sendImageToServer(image);
            SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            penalty.PenaltyTime = new Time(df.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        business.AddPenalty(penalty);
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        ClearColor();
        ClearText();
    }
    public class DownLoadPicture extends AsyncTask<Void, Void, Bitmap> {
        String name;

        public DownLoadPicture(String name) {
            if (name == null) {
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
    public void ClearColor() {
        rdBlue.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        rbYellow.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        rdRed.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }
    public void ClearText() {
        txtName.setText("");
        txtRacerNumber.setText("");
        RadioGroup tentGroup = findViewById(R.id.rgTent);
        tentGroup.clearCheck();
        RadioGroup cardGroup = findViewById(R.id.rgColor);
        cardGroup.clearCheck();
        penalty.ClearPenalty();
        imgRacer.setImageBitmap(d.doInBackground());
    }

    public void SetUpControlers(){
        comments = business.GetComments();
        btnSave = findViewById(R.id.btnSave);
        rdBlue = findViewById(R.id.rdBlue);
        rbYellow = findViewById(R.id.rbYellow);
        rdRed = findViewById(R.id.rdRed);
        rdTent1 = findViewById(R.id.rdTent1);
        rdTent2 = findViewById(R.id.rdTent2);
        txtName = findViewById(R.id.txtRacerName);
        btnTakePic = findViewById(R.id.btnTakePic);
        imgRacer = findViewById(R.id.imgRacer);
        txtRacerNumber = findViewById(R.id.txtRacerNumber);
    }


}