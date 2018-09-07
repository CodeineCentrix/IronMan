package com.example.s216127904.codecentrix;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

import ViewModel.User;

public class Register extends AppCompatActivity {
    EditText txtFullName, txtEmail, txtPassword, txtMatchPassword;
    DBAccess dbAccess = new DBAccess();
    Handler h = new Handler();
    boolean isRegistered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        SetUpControllers();



    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    public void toRegisterRef(View view) throws SQLException {
        if (!txtFullName.getText().equals("")) {
            if (txtEmail.getText().toString().contains("@") && txtEmail.getText().toString().contains(".")) {
                if (txtPassword.getText().toString().equals(txtMatchPassword.getText().toString())) {
                    if (txtPassword.getText().length() > 5) {
                        User.RefPassword = txtPassword.getText().toString();
                        User.RefEmail = txtEmail.getText().toString();
                        User.RefFullName = txtFullName.getText().toString();
                        helpThread save = new helpThread(this.getApplicationContext());
                        new Thread(save).start();
                    } else {
                        Toast.makeText(this, "passwords is short", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "passwords do not match ", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please Enter valid Email", Toast.LENGTH_LONG).show();
            }
        } else

        {
            Toast.makeText(this, "Please Enter name", Toast.LENGTH_LONG).show();
        }

    }
    public void SetUpControllers(){
        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtMatchPassword = findViewById(R.id.txtMatchPassword);

    }
    class helpThread implements Runnable {
        Context context;
        public helpThread(Context context) {
            this.context = context;

        }

        @Override
        public void run() {

            try {
               isRegistered =  dbAccess.AddRef();
                if(isRegistered) {
                    User ref = dbAccess.FindAndLoginUser(User.RefEmail, User.RefPassword);

                    GeneralMethods m = new GeneralMethods(getApplicationContext());
                    m.writeToFile("" + ref.RefID + "," + ref.RefFullName + "," + ref.RefEmail + "," + ref.RefPassword, "user.txt");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            h.post(new Runnable() {
                @Override
                public void run() {
                    if(isRegistered) {
                        try {
                            Intent login = new Intent(context, LoginActivity.class);
                            startActivity(login);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }else {
                        Toast.makeText(context,"Registration failed",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}
