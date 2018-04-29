package com.example.jasim.plateup.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsUserInside extends AppCompatActivity {

    private Button mSubmit;
    private Button mBack;
    private EditText mCurrentPassword;
    private EditText mPassword;
    private EditText mRepeatPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser user;
    private DatabaseReference userRef;

    private ProgressDialog dialog;


    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user_inside);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mPassword = (EditText) findViewById(R.id.password_settings);
        mRepeatPassword = (EditText) findViewById(R.id.repeat_settings);
        mCurrentPassword = (EditText) findViewById(R.id.current_settings);
        mBack = (Button) findViewById(R.id.back_button);
        mSubmit = (Button) findViewById(R.id.save_changes);
        dialog = new ProgressDialog(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(SettingsUserInside.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPassword.getText().toString().equals(mRepeatPassword.getText().toString())){
                    checkLogin();
                }
                else {
                    mRepeatPassword.setError("The passwords does not match!");
                }
            }
        });







    }

    private void checkLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String email = user.getEmail();
            if (mCurrentPassword.getText().toString().length() == 0) {
                mCurrentPassword.setError("Please fill in current password");
            } else if (mPassword.getText().toString().length() < 8 || mRepeatPassword.getText().toString().length() == 0) {
                mRepeatPassword.setError("New password has to be at least 8 characters long");
            } else if (!mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
                mRepeatPassword.setError("Password does not match");
            } else {
                mAuth.signInWithEmailAndPassword(email, mCurrentPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (mCurrentPassword.getText().toString().equals(mPassword.getText().toString())) {
                            Toast.makeText(SettingsUserInside.this, "Change to a password you haven't used before", Toast.LENGTH_LONG).show();
                        } else if (task.isSuccessful()) {
                            updateAccount(mRepeatPassword.getText().toString().trim());
                            onBackPressed();
                            Toast.makeText(SettingsUserInside.this, "Your password has changed!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SettingsUserInside.this, "Current password is wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


    private void updateAccount(String password) {
        if(user != null) {
            System.out.println("SUCCESS");
        }
        user.updatePassword(password);
    }
}
