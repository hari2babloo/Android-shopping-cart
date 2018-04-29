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

public class SettingsAuthActivity extends AppCompatActivity {
    private static String password;
    private Button mBack;
    private EditText mCurrentPassword;
    private Button mSubmit;


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

                    Intent loginIntent = new Intent(SettingsAuthActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_auth);
        mCurrentPassword = (EditText)findViewById(R.id.r_old_password);



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
                checkLogin();
            }
        });

    }

    public static String getPassword() {
        return password;
    }

    private void checkLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String email = user.getEmail();
            if (mCurrentPassword.getText().toString().length() == 0) {
                mCurrentPassword.setError("Please fill in current password");
            } else {
                mAuth.signInWithEmailAndPassword(email, mCurrentPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            password = mCurrentPassword.getText().toString();
                            startActivity(new Intent(SettingsAuthActivity.this, SettingsRest.class));
                        } else {
                            Toast.makeText(SettingsAuthActivity.this, "Password is wrong!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
}
