package com.example.jasim.plateup.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestReg extends AppCompatActivity {


    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mRepeatpassword;
    private EditText mRestaurantTelephone;
    private ArrayList<String> categoryArray = new ArrayList<String>();
    private ArrayList<String> spinnerArray = new ArrayList<>();
    private RecyclerView mContentList;
    private Button mNext;
    private Button mBack;
    private Spinner sItems;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_reg);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        mAuth = FirebaseAuth.getInstance();

        spinnerArray.add("Asian");
        spinnerArray.add("Beverage");
        spinnerArray.add("Burger");
        spinnerArray.add("BBQ/Grill");
        spinnerArray.add("Café");
        spinnerArray.add("Desert Shop");
        spinnerArray.add("Fast Food");
        spinnerArray.add("Indian");
        spinnerArray.add("Italian");
        spinnerArray.add("Kebab");
        spinnerArray.add("Mediterranean");
        spinnerArray.add("Mexican");
        spinnerArray.add("Middle Eastern");
        spinnerArray.add("Pizzeria");
        spinnerArray.add("Salads");
        spinnerArray.add("Seafood");
        spinnerArray.add("Sushi");
        spinnerArray.add("Vegan");
        spinnerArray.add("Other");


        mUsername = (EditText) findViewById(R.id.restname_field);
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText)findViewById(R.id.password_field);
        mRepeatpassword = (EditText) findViewById(R.id.repeat_field);
        mRestaurantTelephone = (EditText) findViewById(R.id.r_telephone);
        mNext = (Button) findViewById(R.id.next_button);
        mBack = (Button) findViewById(R.id.back_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.category_field);
        sItems.setPrompt("Category");
        sItems.setAdapter(adapter);
        mProgress = new ProgressDialog(this);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void startRegister() {
        final String username = mUsername.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        final String repeatPassword = mRepeatpassword.getText().toString().trim();
        final String category = (String)sItems.getSelectedItem();


        if(username.isEmpty() || username.length() < 2) {
            mUsername.setError("Restaurant name must be atleast 2 characters");
        }
        else if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid email address");
        }
        else if(password.isEmpty() || password.length() < 8) {
            mPassword.setError("The password must be atleast 8 characters");
        }
        else if(!password.equals(repeatPassword)) {
            mRepeatpassword.setError("Password does not match");
        }
        else {
            checkUserExists(username, email, password, category);
        }

    }
    public void checkUserExists(final String username, final String email, final String password, final String category) {
        System.out.println("Username: " + username);
        String checkUsername = username.toLowerCase();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        System.out.println(ref);
        ref.orderByChild("checkusername").equalTo(checkUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RestReg.this, "Restaurant name is already in use", Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
                else {
                    mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().getProviders().size() == 1) {
                                    mProgress.dismiss();
                                    Toast.makeText(RestReg.this, "Email already in use", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    ArrayList<String> info = new ArrayList<>();
                                    info.add(username);
                                    info.add(email);
                                    info.add(password);
                                    info.add(category);
                                    startActivity(new Intent(RestReg.this, RestReg2.class).putExtra("info", info));
                                }
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
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
