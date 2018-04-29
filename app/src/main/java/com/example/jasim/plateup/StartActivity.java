/* TODO: PROV AA HA MASSE STATIC VARIABLER HVOR MAN HENTER FRA DATABASEN EN GANG, SLIK AT MAN KAN HENTE DENNE INFORMASJONEN FRA DEN KLASSEN SOM HENTER FRA DATABASEN */


package com.example.jasim.plateup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.jasim.plateup.registration.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(StartActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                else {
                    DatabaseReference test = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                    test.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RestaurantModel model = dataSnapshot.getValue(RestaurantModel.class);
                            if(model == null) {
                                firebaseAuth.signOut();
                            }
                            else if(model.getType().equals("r")) {
                                if(model.getVerified().equals("no")) {
                                    Toast.makeText(StartActivity.this, "Your account is still in the process of getting verified. Please wait", Toast.LENGTH_SHORT).show();
                                    firebaseAuth.signOut();
                                }
                                else {
                                    startActivity(new Intent(StartActivity.this, MainRActivity.class));
                                }
                            }
                            else {
                                startActivity(new Intent(StartActivity.this, MainUActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
