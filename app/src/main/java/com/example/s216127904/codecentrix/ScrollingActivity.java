package com.example.s216127904.codecentrix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import ViewModel.CommentsModel;
import ViewModel.PenaltyModel;
import ViewModel.RacerModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.PERSISTENT_ACTIVITY;

public class ScrollingActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private ImageView imgRacer;
    private ArrayList<CommentsModel> comments;
    private DBAccess business;
    private PenaltyModel penalty = new PenaltyModel();
    private Bitmap bitmapImage;
    private RadioButton rdBlue, rbYellow, rdRed, rdTent1, rdTent2;
    private EditText txtRacerNumber, txtRacerName;
    private TextView tvComment;
    private DownLoadPicture downLoadPicture;
    private LocationManager locationManager;
    private FusedLocationProviderClient client;
    private GeneralMethods generalMethods;
    private ArrayList<RacerModel> racers;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    NavigationView navigationView;
    LinearLayout loImage;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUpControllers();
        generalMethods = new GeneralMethods(getApplicationContext());
        requestPermission();

        txtRacerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               if(!s.toString().equals(""))
               if(Double.parseDouble(s.toString())<9999){
                 int num = Integer.parseInt(s.toString());
                 String name =  binarySearch(racers,0,racers.size()-1,num);
                 txtRacerName.setText(name);
               }
            }
        });
        downLoadPicture = new DownLoadPicture(null);
        imgRacer.setImageBitmap(downLoadPicture.doInBackground());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                business = new DBAccess();
                comments = business.GetComments();
                racers = business.GetAllRacerers();
            }
        });
    }
    public void onTakePicture(View view){
       Intent showCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       startActivityForResult(showCamera, 10);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLoction();
            }
        }
    }
    public void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);


    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdBlue:
                if (checked)
                    showDialog(view, 3);

                break;
            case R.id.rbYellow:
                if (checked)
                    showDialog(view, 2);

                break;
            case R.id.rdRed:
                if (checked)
                    showDialog(view, 1);

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
                    penalty.TentID = 2;
                break;
        }
    }
    public void getLoction(){
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        client.getLastLocation().addOnSuccessListener(ScrollingActivity.this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location !=null) {
                            penalty.longitude = location.getLongitude();
                            penalty.latitude = location.getAltitude();
                        }
                    }
                });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10 && data!=null){
            bitmapImage = (Bitmap) data.getExtras().get("data");
            imgRacer.setImageBitmap(bitmapImage);
            try {
                penalty.sendImageToServer(bitmapImage);
            }catch (IOException e){
                e.printStackTrace();
            }

            loImage.setVisibility(navigationView.VISIBLE);
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
                tvComment = findViewById(R.id.tvComment);
                tvComment.setText(listAdapter.comments.get(position).CommentDescription);
                tvComment.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void ToSavePenalty(View v) {
        String i = txtRacerNumber.getText().toString();
        int refid;
        try {
             refid = Integer.parseInt(generalMethods.Read("user.txt", ",")[0]);
        }catch (Exception e){refid=1;}
        try{
            penalty.RefID = refid;
            penalty.RacerID = Integer.parseInt(i);////////////////////////////////////
            if(bitmapImage !=null)
                penalty.sendImageToServer(bitmapImage);
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
    public void ClearText(){
        loImage.setVisibility(View.GONE);
        txtRacerName.setText("");
        txtRacerNumber.setText("");
        tvComment.setVisibility(View.GONE);
        RadioGroup tentGroup = findViewById(R.id.rgTent);
        tentGroup.clearCheck();
        RadioGroup cardGroup = findViewById(R.id.rgColor);
        cardGroup.clearCheck();
        penalty.ClearPenalty();
        imgRacer.setImageBitmap(downLoadPicture.doInBackground());
    }
    public void SetUpControllers(){
        loImage = findViewById(R.id.loImage);
        txtRacerNumber = findViewById(R.id.txtRacerNumber);
        txtRacerName = findViewById(R.id.txtRacerName);
        rdBlue = findViewById(R.id.rdBlue);
        rbYellow = findViewById(R.id.rbYellow);
        rdRed = findViewById(R.id.rdRed);
        rdTent1 = findViewById(R.id.rdTent1);
        rdTent2 = findViewById(R.id.rdTent2);
        imgRacer = findViewById(R.id.imgRacer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        rdBlue.setBackgroundColor(getResources().getColor(R.color.blue));
        rbYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
        rdRed.setBackgroundColor(getResources().getColor(R.color.red));
    }
    public String binarySearch( ArrayList<RacerModel> racers, int l, int r, int x){
        if (r>=l)
        {
            int mid = l + (r - l)/2;

            // If the element is present at the
            // middle itself
            if (racers.get(mid).RacerID == x)
                return racers.get(mid).RacerName;

            // If element is smaller than mid, then
            // it can only be present in left subarray
            if (racers.get(mid).RacerID > x)
                return binarySearch(racers, l, mid-1, x);

            // Else the element can only be present
            // in right subarray
            return binarySearch(racers, mid+1, r, x);
        }

        // We reach here when element is not present
        //  in array
        return "";
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Map) {
            Intent ShowMap = new Intent(this,Map.class);
            startActivity(ShowMap);
        } else if (id == R.id.nav_Theme) {

        } else if (id == R.id.nav_Help) {

        } else if (id == R.id.nav_SignOut) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void ClearImage(View view){
        loImage.setVisibility(View.GONE);
    }
}