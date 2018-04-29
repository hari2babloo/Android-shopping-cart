package com.example.jasim.plateup.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jasim.plateup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LostPasswordActivity extends AppCompatActivity {



    private EditText mEmail;

    private Button mNext;
    private Button mBack;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_password);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.email_field);

        mNext = (Button) findViewById(R.id.next_button);
        mBack = (Button) findViewById(R.id.back_button);


        mProgress = new ProgressDialog(this);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMessage("Please wait");
                mProgress.show();
                mAuth.sendPasswordResetEmail(mEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LostPasswordActivity.this, "Reset password link has been sent to your email", Toast.LENGTH_LONG).show();
                                    mProgress.dismiss();
                                    startActivity(new Intent(LostPasswordActivity.this, MainActivity.class));
                                }
                                else {
                                    mProgress.dismiss();
                                    mEmail.setError("That email does not exist in our records");
                                }
                            }
                        });
            }
        });
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
