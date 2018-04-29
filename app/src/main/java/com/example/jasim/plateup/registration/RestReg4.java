package com.example.jasim.plateup.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RestReg4 extends AppCompatActivity {


    private Button mNext;
    private Button mBack;
    private FloatingActionButton mInfo;
    private ArrayList<String> info = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_reg4);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Bundle extras = getIntent().getExtras();
        info = extras.getStringArrayList("info");
        System.out.println("STORRELSEN PAA INFO pAA STARTEN" + info.size());

        mNext = (Button) findViewById(R.id.next_button);
        mProgress = new ProgressDialog(this);
//        mBack = (Button) findViewById(R.id.back_button);
//
//
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });


    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radioButton2:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
    private void startRegister() {
        System.out.println("STORRELSEN PAA INFO pAA SLUTTEN" + info.size());
        mProgress.setMessage("Signing up..");
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(info.get(1),info.get(2)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    mAuthListener = new FirebaseAuth.AuthStateListener() {

                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null) {
                                user.sendEmailVerification();
                            }
                        }
                    };
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = mDatabase.child(user_id);


                    // TODO:  Lage et restaurant objekt, saa laste opp til firebase samtidig.
                    currentUserDb.child("id").setValue(user_id);
                    currentUserDb.child("username").setValue(info.get(0));
                    currentUserDb.child("checkusername").setValue(info.get(0).toLowerCase());
                    currentUserDb.child("email").setValue(info.get(1));
                    currentUserDb.child("category").setValue(info.get(3));
                    currentUserDb.child("address").setValue(info.get(4));
                    currentUserDb.child("telephone").setValue(info.get(5));
                    currentUserDb.child("bankaccount").setValue(info.get(6));
                    currentUserDb.child("amountopenings").setValue(info.get(7));
                    currentUserDb.child("waittime").setValue("15");

                    currentUserDb.child("openings").child("first").child("fromDay").setValue(info.get(8));
                    currentUserDb.child("openings").child("first").child("toDay").setValue(info.get(9));
                    currentUserDb.child("openings").child("first").child("open").setValue(info.get(10) + info.get(11));
                    currentUserDb.child("openings").child("first").child("close").setValue(info.get(12) + info.get(13));
                    if(Integer.parseInt(info.get(9)) > 1) {
                        currentUserDb.child("openings").child("second").child("fromDay").setValue(info.get(14));
                        currentUserDb.child("openings").child("second").child("toDay").setValue(info.get(15));
                        currentUserDb.child("openings").child("second").child("open").setValue(info.get(16) + info.get(17));
                        currentUserDb.child("openings").child("second").child("close").setValue(info.get(18) + info.get(19));
                        if(Integer.parseInt(info.get(9)) == 3){
                            currentUserDb.child("openings").child("third").child("fromDay").setValue(info.get(20));
                            currentUserDb.child("openings").child("third").child("toDay").setValue(info.get(21));
                            currentUserDb.child("openings").child("third").child("open").setValue(info.get(22) + info.get(23));
                            currentUserDb.child("openings").child("third").child("close").setValue(info.get(24) + info.get(25));
                        }
                    }
                    currentUserDb.child("status").setValue("open");


                    currentUserDb.child("type").setValue("r");
                    currentUserDb.child("rating").setValue("0");
                    currentUserDb.child("amountraters").setValue("0");

                    mProgress.dismiss();

                    Intent mainIntent = new Intent(RestReg4.this, MainRActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }
        });
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
