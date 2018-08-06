package com.example.s216127904.codecentrix;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ViewModel.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    ProgressDialog progressDialog;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    private View mLoginFormView;
    private CheckBox cbRemeber;
    private GeneralMethods m;
    String[] details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        cbRemeber = findViewById(R.id.cbRemeber);
        m = new GeneralMethods(getApplicationContext());
        mEmailView = (AutoCompleteTextView) findViewById(R.id.input_email);
        mPasswordView = (EditText) findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressView = findViewById(R.id.login_progress);
                attemptLogin();

            }
        });
        RemeberMe();
        if(cbRemeber.isChecked()){
            details = m.Read("user.txt",",");
            mEmailView.setText(details[2]);
            mPasswordView.setText(details[3]);
        }

        mLoginFormView = findViewById(R.id.login_form);

    }




    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
       //Ask for permissions in here

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //Add bigger spinner
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.Theme_AppCompat_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            if(mEmailView.getContext() != null ) {
            //    progressDialog.show(); // if fragment use getActivity().isFinishing() or isAdded() method
            }
            //End big spinner
            Context c = getApplicationContext();
            mAuthTask = new UserLoginTask(email, password, c,mProgressView);
            mAuthTask.execute();
           boolean grantAccess=  mAuthTask.doInBackground();

           if (grantAccess){
              // progressDialog.dismiss();
               Intent penaltySession = new Intent(getApplicationContext(), ScrollingActivity.class);
               startActivity(penaltySession);

           }else  if (grantAccess==false){

           }
        }
    }

    public void RemeberMe(String answ){
        m.writeToFile(answ,"Remember.txt");
    }
    public void RemeberMe(){
        if(m.readFromFile("Remember.txt").equals("yes"))
            cbRemeber.setChecked(true);
        else
            cbRemeber.setChecked(false);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
   @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            if (show) {
                mProgressView.setVisibility(View.VISIBLE);

            }else
            {
                mProgressView.setVisibility(View.GONE);

            }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Context c;
        private  ProgressBar br;
        UserLoginTask(String email, String password, Context a, ProgressBar bar) {
            mEmail = email;
            mPassword = password;
            c = a;
            br= bar;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            boolean isValidUser;
            try {

                DBAccess database = new DBAccess();
                User ref =  database.FindAndLoginUser(mEmail,mPassword);
                GeneralMethods m = new GeneralMethods(getApplicationContext());
                m.writeToFile(""+ref.RefID+","+ref.RefFullName+","+ref.RefEmail+","+ref.RefPassword,"user.txt");
                if(cbRemeber.isChecked())
                    RemeberMe("yes");
                else
                    RemeberMe("no");
                isValidUser = ref.RefID>0;
            } catch (Exception a ) {
                return false;
            }

            return isValidUser;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            progressDialog.dismiss();
            if (success) {

                finish();
            } else {
//                Intent showMain = new Intent(getApplicationContext(),ScrollingActivity.class);
//                startActivity(showMain);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }


    }
}

