package com.example.s216127904.codecentrix;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.net.Uri;
import ViewModel.CommentsModel;
import ViewModel.PenaltyModel;
import ViewModel.RacerModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.util.Date;

public class ScrollingActivity extends AppCompatActivity  implements IgetComment,NavigationView.OnNavigationItemSelectedListener{
    public static final int CAMERA_REQUEST_CODE = 10;
    public static final int PERMISSION_REQUST_CODE = 131;
    public static final int LOCATION_REQUEST_CODE = 1;
    public static final int GELLARY_REQUEST_CODE = 619;
    private PhotoView imgRacer;
    private ArrayList<CommentsModel> comments;
    private DBAccess business;
    private PenaltyModel penalty = new PenaltyModel();
    private Bitmap bitmapImage;
    private EditText txtRacerNumber, txtRacerName,txtRacerSurname;
    private DownLoadPicture downLoadPicture;
    private GeneralMethods generalMethods;
    private ArrayList<RacerModel> racers;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Button btnSave;
    private NavigationView navigationView;
    private LinearLayout loImage, loHideCards;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private ImageView tent1,tent2,imgBlue,imgRed,imgYellow,tent1X,tent2X;
    private View vBlue,vYellow,vRed;
    private File picturesDirectory , imageFile;
    private Uri pictureUri;
    Context thisThing;
    Spinner spnComment;
    CommentAdapter listAdapter;
    Switch autoCamSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUpControllers();
        thisThing = this;
        tent2X = findViewById(R.id.imgtentTwoX);
        tent1X = findViewById(R.id.imgtentOneX);
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
                    LinearLayout l1 = findViewById(R.id.l1);
                    LinearLayout l2 = findViewById(R.id.l2);
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
        LinearLayout racerDetailsLayout = findViewById(R.id.linearLayout);
        Animation upToDown = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        racerDetailsLayout.setAnimation(upToDown);
        SetHeader();
        loHideCards.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               onKeyBoardHid(v);
                return true;
            }
        });
        onKeyBoardHid(loHideCards);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(comments!=null){
                   txtRacerNumber.requestFocus();
                }
            }
        },5000);
        spnComment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                penalty.CommentID = listAdapter.getCommentID(position) ;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.auto_cam);
        autoCamSwitch = (Switch) item.getActionView().findViewById(R.id.drawer_switch);
        if(generalMethods.readFromFile(getString(R.string.camera_file_name)).equals("on")){
            onTakePicture(btnSave);
            autoCamSwitch.setChecked(true);
        }

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
        TextView tvFullName = header.findViewById(R.id.navUserName);
        TextView tvEmail = header.findViewById(R.id.navEmail);
        tvEmail.setText(details[2]);
        tvFullName.setText(details[1]);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void onTakePicture(View view){
       /* Intent showCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureName = getPictureName();
        File imageFile = new File(pictureDirectory, pictureName);
        Uri pictureUri = Uri.fromFile(imageFile);
        showCamera.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        startActivityForResult(showCamera, CAMERA_REQUEST_CODE);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                invokeCamera();
            }else {

                String[] request = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(request, PERMISSION_REQUST_CODE);

            }
        }


    }
    private void invokeCamera() {

        // get a file reference
         pictureUri = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider", createImageFile());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // tell the camera where to save the image.
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        // tell the camera to request WRITE permission.
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }
    public void onImageGalleryClicked(View v) {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, GELLARY_REQUEST_CODE);
    }
    private File createImageFile() {
        // the public picture director
         picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // timestamp makes unique name.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        // put together the directory and the timestamp to make a unique image location.
         imageFile = new File(picturesDirectory, "picture" + timestamp + ".jpg");

        return imageFile;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                invokeCamera();
            }else {
                Toast.makeText(this, R.string.can_open_without,Toast.LENGTH_LONG).show();
            }
        }

    }
    public void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION,WRITE_EXTERNAL_STORAGE}, LOCATION_REQUEST_CODE);


    }
    public void onKeyBoardHid(View view){
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(view.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void onCardSelect(View view) {
        // Is the button now checked?


        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(view.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        imgBlue.setVisibility(View.GONE);
        imgRed.setVisibility(View.GONE);
        imgYellow.setVisibility(View.GONE);
        vBlue.setBackgroundColor(getResources().getColor(R.color.blue));
        vYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
        vRed.setBackgroundColor(getResources().getColor(R.color.red));
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.vBlue:
                vBlue.setBackgroundColor(getResources().getColor(R.color.SelectedBlue));
                showDialog(view, 3);
                imgBlue.setVisibility(View.VISIBLE);
                break;
            case R.id.vYellow:
                vYellow.setBackgroundColor(getResources().getColor(R.color.SelectedYellow));
                showDialog(view, 2);
                imgYellow.setVisibility(View.VISIBLE);
                break;
            case R.id.vRed:
                vRed.setBackgroundColor(getResources().getColor(R.color.SelectedRed));
                showDialog(view, 1);
                imgRed.setVisibility(View.VISIBLE);
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
        tent1X.setVisibility(View.INVISIBLE);
        tent2X.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.imgTent1:
                tent1X.setVisibility(View.VISIBLE);
                penalty.TentID = 1;
                tent1.setBackgroundColor(getResources().getColor(R.color.colorWhitish));
                tent2.setBackgroundColor(getResources().getColor(R.color.background));
                break;
            case R.id.imgTent2:
                    tent2X.setVisibility(View.VISIBLE);
                    tent1.setBackgroundColor(getResources().getColor(R.color.background));
                    tent2.setBackgroundColor(getResources().getColor(R.color.colorWhitish));
                    penalty.TentID = 2;
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(requestCode==10 && data!=null){
            bitmapImage = (Bitmap) data.getExtras().get("data");
            imgRacer.setImageBitmap(bitmapImage);
            loImage.setVisibility(navigationView.VISIBLE);

        }else*/
        if(resultCode == RESULT_OK) {
                if (requestCode == CAMERA_REQUEST_CODE && pictureUri!=null) {
                    Toast.makeText(this, "Image Saved.", Toast.LENGTH_LONG).show();

                    Uri uri = pictureUri;
                    // declare a stream to read the image data from the SD Card.
                    InputStream inputStream;

                    // we are getting an input stream, based on the URI of the image.
                    try {
                        inputStream = getContentResolver().openInputStream(uri);

                        // get a bitmap from the stream.
                        bitmapImage = BitmapFactory.decodeStream(inputStream);



                        // show the image to the user
                        imgRacer.setImageBitmap(bitmapImage);

                        loImage.setVisibility(View.VISIBLE);
                        if(racers==null) {
                            helpThread helpThread = new helpThread(true);
                            new Thread(helpThread).start();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        // show a message to the user indictating that the image is unavailable.
                        Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                    }

                }
                // if we are here, everything processed successfully.
                if (requestCode == GELLARY_REQUEST_CODE) {
                    // if we are here, we are hearing back from the image gallery.

                    // the address of the image on the SD Card.
                    Uri imageUri = data.getData();

                    // declare a stream to read the image data from the SD Card.
                    InputStream inputStream;

                    // we are getting an input stream, based on the URI of the image.
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);

                        // get a bitmap from the stream.
                        Bitmap image = BitmapFactory.decodeStream(inputStream);


                        // show the image to the user
                        imgRacer.setImageBitmap(image);
                        loImage.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        // show a message to the user indictating that the image is unavailable.
                        Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                    }

                }
            }
        }
    public void showDialog(View view, int ticketID) {
        penalty.TicketID = ticketID;/////////////////////////////
       listAdapter = new CommentAdapter(getApplicationContext(),this, comments, ticketID);
        spnComment.setAdapter(listAdapter);


    }
    public void ToSavePenalty(View v) {
        String i = txtRacerNumber.getText().toString();
        penalty.confirmationSurname = txtRacerSurname.getText().toString();
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
    @Override
    public void SetCommentID(int value) {
        penalty.CommentID = value;
    }
    @Override
    public String GetCommentID() {
        return null;
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
        txtRacerSurname.setText("");
        imgBlue.setVisibility(View.GONE);
        imgRed.setVisibility(View.GONE);
        imgYellow.setVisibility(View.GONE);

        vBlue.setBackgroundColor(getResources().getColor(R.color.blue));
        vYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
        vRed.setBackgroundColor(getResources().getColor(R.color.red));
        loHideCards.setVisibility(View.GONE);
        penalty.ClearPenalty();
        if(generalMethods.readFromFile(getString(R.string.camera_file_name)).equals("on")){
            onTakePicture(btnSave);
        }
    }
    public void SetUpControllers(){
        loImage = findViewById(R.id.loImage);
        txtRacerNumber = findViewById(R.id.txtRacerNumber);
        txtRacerName = findViewById(R.id.txtRacerName);
        txtRacerSurname = findViewById(R.id.txtRacerSurname);
        imgBlue = findViewById(R.id.imgBlue);
        imgRed = findViewById(R.id.imgRed);
        imgYellow = findViewById(R.id.imgYellow);
        vBlue = findViewById(R.id.vBlue);
        vYellow = findViewById(R.id.vYellow);
        vRed = findViewById(R.id.vRed);
        imgRacer = findViewById(R.id.imgRacer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        loHideCards = findViewById(R.id.loHideCards);
        spnComment = findViewById(R.id.spnComment);
        btnSave = findViewById(R.id.btnSave);
        navigationView = findViewById(R.id.nav_view);
        tent1 = findViewById(R.id.imgTent1);
        tent2 = findViewById(R.id.imgTent2);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.auto_cam);
        autoCamSwitch = (Switch) item.getActionView().findViewById(R.id.drawer_switch);
        tent2.setBackgroundColor(getResources().getColor(R.color.background));
        tent1.setBackgroundColor(getResources().getColor(R.color.background));
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Map) {
            Intent ShowMap = new Intent(this,Map.class);
            startActivity(ShowMap);
        }else if(id == R.id.auto_cam){

                    if(!autoCamSwitch.isChecked()){
                        generalMethods.writeToFile("on",getString(R.string.camera_file_name));
                        autoCamSwitch.setChecked(true);
                    }else {
                        generalMethods.writeToFile("off",getString(R.string.camera_file_name));
                        autoCamSwitch.setChecked(false);
                    }
        }
        else if (id == R.id.nav_Help) {
            onImageGalleryClicked(btnSave);
        } else if (id == R.id.nav_SignOut) {
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void ClearImage(View view){
        loImage.setVisibility(View.GONE);
    }
    class helpThread implements Runnable {
        PenaltyModel penalty;
        boolean onCreate;
        boolean isConnecting;
        Snackbar mySnackbar;
        public helpThread(PenaltyModel penalty) {
            this.penalty = penalty;
        }

        public helpThread(boolean onCreate) {
            this.onCreate = onCreate;
        }


        @Override
        public void run() {
            business = new DBAccess();
            isConnecting = business.isConnecting();
            if(onCreate){
                if(isConnecting) {
                    comments = business.GetComments();
                    racers = business.GetAllRacerers();
                }else {
                    mySnackbar = Snackbar.make(vBlue,"No Connection", 3000);
                    mySnackbar.getView().setBackgroundColor(Color.RED);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mySnackbar.show();
                            InputMethodManager imm = (InputMethodManager)getSystemService(vBlue.getContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(vBlue.getWindowToken(), 0);
                            try {
                                Thread.sleep(3000);
                                helpThread h = new helpThread(true);
                                new Thread(h).start();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }else if(isConnecting) {
                isConnecting = business.AddPenalty(penalty);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnecting) {
                            Toast.makeText(getApplicationContext(), "Ensure data is on and try again", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                            ClearText();
                            txtRacerNumber.requestFocus();
                        }
                        progressDialog.dismiss();
                    }

                });
            }else {
                mySnackbar = Snackbar.make(vBlue,"No Connection", 3000);
                mySnackbar.getView().setBackgroundColor(Color.RED);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mySnackbar.show();
                        progressDialog.dismiss();
                    }
                });

            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }
}
