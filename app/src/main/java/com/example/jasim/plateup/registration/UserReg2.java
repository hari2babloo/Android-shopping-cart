package com.example.jasim.plateup.registration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserReg2 extends AppCompatActivity {


    private static final String TAG = "UserReg";
    private EditText mEmail;
    private EditText mPassword;
    private EditText mRepeatpassword;

    private FloatingActionButton mInfo;

    private Button mUser_reg_btn;
    private Button mBackButton;
    private Button mOk;

    private TextView mTerms;
    private CheckBox mCheckBox;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String user;
    private boolean pass;
    //Popup window variables
    private HashMap<String, String> userModel;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg2);
        // popup
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        mRepeatpassword = (EditText) findViewById(R.id.repeat_field);

        final LayoutInflater timeInflater = LayoutInflater.from(this);
        final View timeView = timeInflater.inflate(R.layout.address_info, null);
        final Dialog dialogInfo = new Dialog(this);

        dialogInfo.setContentView(timeView);
        //mOk = (Button)timeView.findViewById(R.id.ok);
//        mOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogInfo.dismiss();
//            }
//        });
        mUser_reg_btn = (Button) findViewById(R.id.register_eater);
        mBackButton = (Button) findViewById(R.id.back_button);
        mTerms = (TextView) findViewById(R.id.terms);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mProgress = new ProgressDialog(this);
        final Dialog dialog = new Dialog(this);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.custom_dialog);
                dialog.show();
            }
        });


        mUser_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String repeatPassword = mRepeatpassword.getText().toString().trim();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email address");
        }

        else if (password.isEmpty() || password.length() < 8) {
            mPassword.setError("The password must be atleast 8 characters");
        } else if (!password.equals(repeatPassword)) {
            System.out.println(password);
            System.out.println(repeatPassword);
            mRepeatpassword.setError("Password does not match");
        } else if (!mCheckBox.isChecked()) {
            mCheckBox.setError("Accept Terms and policy");
        } else {
            mProgress.setMessage("Signing up..");
            mProgress.show();
            checkUserExists(email, password);
        }
    }

    public void checkUserExists(final String email, final String password) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent userReg = getIntent();
        userModel = (HashMap<String, String>)userReg.getSerializableExtra("userreg");
        ref.orderByChild("checkusername").equalTo(userModel.get("checkusername")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    Toast.makeText(UserReg2.this, "Username already in use", Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = mDatabase.child(user_id);

                                currentUserDb.setValue(userModel);

                                Intent mainIntent = new Intent(UserReg2.this, MainUActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(UserReg2.this, "Email already in use", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}