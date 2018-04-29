package com.example.jasim.plateup.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class SettingsRestPass extends AppCompatActivity {
    private Button mBack;
    private EditText mCurrentPassword;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private Button mSubmit;

    private String email;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(SettingsRestPass.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_rest_pass);
        mCurrentPassword = (EditText)findViewById(R.id.r_old_password);
        mPassword = (EditText)findViewById(R.id.r_password);
        mRepeatPassword = (EditText)findViewById(R.id.r_repeat);



        mBack = (Button)findViewById(R.id.back_button);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmit = (Button)findViewById(R.id.save_changes);
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
             if (mPassword.getText().toString().length() < 8 || mRepeatPassword.getText().toString().length() == 0) {
                mRepeatPassword.setError("New password has to be at least 8 characters long");
            } else if (!mPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
                mRepeatPassword.setError("Password does not match");
            } else {
                mAuth.signInWithEmailAndPassword(email, SettingsAuthActivity.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(SettingsAuthActivity.getPassword().equals(mPassword.getText().toString())) {
                            Toast.makeText(SettingsRestPass.this, "Change to a password you haven't used before", Toast.LENGTH_SHORT).show();
                        }
                        else if (task.isSuccessful()) {
                            updateAccount(mRepeatPassword.getText().toString().trim());
                            onBackPressed();
                            Toast.makeText(SettingsRestPass.this, "Your password has changed!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SettingsRestPass.this, "Current password is wrong!", Toast.LENGTH_LONG).show();
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
