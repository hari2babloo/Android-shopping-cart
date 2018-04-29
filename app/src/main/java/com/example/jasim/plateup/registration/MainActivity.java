package com.example.jasim.plateup.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.Check.Connection;
import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.Restaurant;
import com.example.jasim.plateup.RestaurantModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private ProgressDialog dialog;

    private TextView mForgotPassword;
    private TextView mCreateAccount;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private Button mUserRegButton;
    private Button mRestRegButton;
    private MenuItem mRestButton;

    public MainActivity() {
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    DatabaseReference test = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    test.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RestaurantModel model = dataSnapshot.getValue(RestaurantModel.class);
                            if(model.getType().equals("r")) {
                                if(model.getVerified().equals("no")) {
                                    Toast.makeText(MainActivity.this, "Your account is still in the process of getting verified. Please wait", Toast.LENGTH_LONG).show();
                                    firebaseAuth.signOut();
                                }
                                else {
                                    startActivity(new Intent(MainActivity.this, MainRActivity.class));
                                }
                            }
                            else {
                                startActivity(new Intent(MainActivity.this, MainUActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        mDatabase.keepSynced(true);
        mUsername = (EditText) findViewById(R.id.restname_field);
        mPassword = (EditText)findViewById(R.id.password_field);
        mLoginButton = (Button) findViewById(R.id.next_button);

        mCreateAccount = (TextView) findViewById(R.id.create_account);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);

        //mRestRegButton = (Button) findViewById(R.id.create_rest);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        /*mUserRegButton = (Button)findViewById(R.id.reg_user);
        mRestRegButton = (Button)findViewById(R.id.reg_restaurant);*/


        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, mCreateAccount);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Eater")) {
                            startActivity(new Intent(MainActivity.this, UserReg.class));
                        }
                        else if(item.getTitle().equals("Restaurant")) {
                            startActivity(new Intent(MainActivity.this, RestReg.class));
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LostPasswordActivity.class));
            }
        });

    }

    private void checkLogin() {

        if(new Connection(this).getConnected()) {

            String email = mUsername.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if(isGooglePlayServicesAvailable(this)) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    System.out.println("mAuth: " + mAuth);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkUserType();
                            } else {
                                Toast.makeText(MainActivity.this, "Your email or password is incorrect. Try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            else {
                Toast.makeText(this, "Open google play", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    private void checkUserType() {
        System.out.println(mDatabase);
        DatabaseReference mRef = mDatabase.child(mAuth.getCurrentUser().getUid());
        System.out.println(mRef);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RestaurantModel model = dataSnapshot.getValue(RestaurantModel.class);
                if(model.getType().equals("r")) {
                    if(model.getVerified().equals("yes")) {
                        startActivity(new Intent(MainActivity.this, MainRActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }
                else {
                    startActivity(new Intent(MainActivity.this, MainUActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
