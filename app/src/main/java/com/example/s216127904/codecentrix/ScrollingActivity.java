package com.example.s216127904.codecentrix;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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

import com.github.chrisbanes.photoview.PhotoView;
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

public class ScrollingActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private PhotoView imgRacer;
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
    private Button btnSave;
    NavigationView navigationView;
    LinearLayout loImage, loHideCards;
    ProgressDialog progressDialog;
    Handler handler = new Handler();
    private ImageView tent1,tent2;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUpControllers();
        generalMethods = new GeneralMethods(getApplicationContext());
        requestPermission();
        progressDialog = new ProgressDialog(ScrollingActivity.this,
                R.style.Theme_AppCompat_Dialog);
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
                    if(Double.parseDouble(s.toString())<9999 && racers!=null){
                        int num = Integer.parseInt(s.toString());
                        String name =  binarySearch(racers,0,racers.size()-1,num);
                        txtRacerName.setText(name);
                        if(name.equals("")) {
                            loHideCards.setVisibility(View.GONE);
                            btnSave.setEnabled(false);
                        }else{
                            loHideCards.setVisibility(View.VISIBLE);
                        }
                    }else {
                        txtRacerName.setText("");
                        loHideCards.setVisibility(View.GONE);
                    }
            }
        });
        txtRacerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().equals("")) {
                    LinearLayout l1 = (LinearLayout) findViewById(R.id.l1);
                    LinearLayout l2 = (LinearLayout) findViewById(R.id.l2);
                    Animation upToDown = AnimationUtils.loadAnimation(txtRacerName.getContext(), R.anim.downtoup);
                    l1.setAnimation(upToDown);
                    Animation downToUp = AnimationUtils.loadAnimation(txtRacerName.getContext(), R.anim.downtoup);
                    l2.setAnimation(downToUp);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        downLoadPicture = new DownLoadPicture(null);
        imgRacer.setImageBitmap(downLoadPicture.doInBackground());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        helpThread h = new helpThread(true);
        new Thread(h).start();
        LinearLayout l = (LinearLayout) findViewById(R.id.linearLayout);
        Animation upToDown = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l.setAnimation(upToDown);
        SetHeader();
    }
    public void SetHeader(){
        String[] details = generalMethods.Read("user.txt",",");
        NavigationView navigationView = findViewById(R.id.nav_view);


        View header = navigationView.getHeaderView(0);
//        ImageView profile = header.findViewById(R.id.nav_imgIcon);
//        try {
//            //  DownLoadPicture d = new DownLoadPicture("meter");
//            //  profile.setImageBitmap(d.doInBackground());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        TextView tvFullName = (TextView) header.findViewById(R.id.navUserName);
        TextView tvEmail = header.findViewById(R.id.navEmail);
        tvEmail.setText(details[2]);
        tvFullName.setText(details[1]);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(view.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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
        if(txtRacerNumber.isFocused())
            txtRacerNumber.clearFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            btnSave.setFocusedByDefault(true);
        }
        btnSave.setEnabled(true);
        btnSave.setVisibility(View.VISIBLE);
//        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.imgTent1:
                //if (checked)
                    penalty.TentID = 1;
                tent1.setBackgroundColor(getResources().getColor(R.color.background));
                tent2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.imgTent2:
               // if (checked)
                    tent1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    tent2.setBackgroundColor(getResources().getColor(R.color.background));
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

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                penalty.CommentID = listAdapter.getCommentID(position);//////////////////////////////////////////////////
                tvComment = findViewById(R.id.tvComment);
                tvComment.setText("SELECTED COMMENT:\n "+listAdapter.comments.get(position).CommentDescription);
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
            SimpleDateFormat df = new SimpleDateFormat("kk:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            penalty.PenaltyTime = new Time(df.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(penalty.CommentID!=0) {
            progressDialog.show();
            progressDialog.setCancelable(false);
            helpThread h = new helpThread(penalty);
            new Thread(h).start();
        }else{
            Toast.makeText(getApplicationContext(), "Select A card then Comment", Toast.LENGTH_SHORT).show();
        }
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
        btnSave.setVisibility(View.GONE);
        txtRacerName.setText("");
        txtRacerNumber.setText("");
        tvComment.setVisibility(View.GONE);

        RadioGroup cardGroup = findViewById(R.id.rgColor);
        cardGroup.clearCheck();
        loHideCards.setVisibility(View.GONE);
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

        imgRacer = findViewById(R.id.imgRacer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        loHideCards = findViewById(R.id.loHideCards);
        btnSave = findViewById(R.id.btnSave);
        navigationView = findViewById(R.id.nav_view);
        tent1 = findViewById(R.id.imgTent1);
        tent2 = findViewById(R.id.imgTent2);
        setSupportActionBar(toolbar);
//        rdBlue.setBackgroundColor(getResources().getColor(R.color.blue));
//        rbYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
//        rdRed.setBackgroundColor(getResources().getColor(R.color.red));
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
    class helpThread implements Runnable {
        PenaltyModel penalty;
        boolean onCreate;
        public helpThread(PenaltyModel penalty) {
            this.penalty = penalty;
        }

        public helpThread(boolean onCreate) {
            this.onCreate = onCreate;
        }


        @Override
        public void run() {
            if(onCreate){
                business = new DBAccess();
                comments = business.GetComments();
                racers = business.GetAllRacerers();
            }else {
                final boolean isConnecting = business.AddPenalty(penalty);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnecting) {
                            Toast.makeText(getApplicationContext(), "Ensure data is on and try again", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                            ClearText();
                        }
                        progressDialog.dismiss();
                    }

                });
            }
        }
    }
}