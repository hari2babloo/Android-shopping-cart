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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsRestBank extends AppCompatActivity {
    private Button mBack;

    private EditText rOldPassword;
    private EditText rBankAccount;
    private EditText rBankRepeat;
    private Button rSave;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private DatabaseReference bankRef;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(SettingsRestBank.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_rest_bank);

        rOldPassword = (EditText)findViewById(R.id.r_old_password);
        rBankAccount = (EditText)findViewById(R.id.r_bank);
        rBankRepeat = (EditText) findViewById(R.id.r_bank_repeat);


        mBack = (Button)findViewById(R.id.back_button);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rSave = (Button)findViewById(R.id.save_changes);
        rSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

    }


    private void checkLogin() {
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("email");
        bankRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("bankaccount");

        if (!rBankAccount.getText().toString().equals(rBankRepeat.getText().toString())) {
            rBankRepeat.setError("Bank account does not match");
        }

        bankRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(rBankRepeat.getText().toString().equals(rBankAccount.getText().toString())) {
                    String bankAccount = rBankAccount.getText().toString();
                    dataSnapshot.getRef().setValue(bankAccount);
                    Toast.makeText(SettingsRestBank.this, "Your bank account has been updated", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
