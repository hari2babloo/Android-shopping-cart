package com.example.jasim.plateup.registration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.jasim.plateup.RestaurantModel;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class UserReg extends AppCompatActivity {


    private static final String TAG = "UserReg";
    private EditText mUsername;
    private Button mAddress;
    private EditText mPhoneNumber;

    private TextView addressField;
    private TextView postal;
    private TextView city;
    private TextView country;

    private FloatingActionButton mInfo;

    private Button mUser_reg_btn;
    private Button mBackButton;
    private Button mOk;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String user;
    private boolean pass;

    private Place place;

    RestaurantModel userModel = new RestaurantModel();
    //Popup window variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg);
        // popup
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsername = (EditText) findViewById(R.id.restname_field);
        mAddress = (Button) findViewById(R.id.address_btn);
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(mAddress);
            }
        });
        mInfo = (FloatingActionButton)findViewById(R.id.info_address);

        addressField = (TextView)findViewById(R.id.address);
        postal = (TextView)findViewById(R.id.postal);
        city = (TextView)findViewById(R.id.city);
        country = (TextView)findViewById(R.id.country);

        final LayoutInflater timeInflater = LayoutInflater.from(this);
        final View timeView = timeInflater.inflate(R.layout.address_info, null);
        final Dialog dialogInfo = new Dialog(this);

        dialogInfo.setContentView(timeView);
        //mOk = (Button)timeView.findViewById(R.id.ok);
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfo.show();
            }
        });
//        mOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogInfo.dismiss();
//            }
//        });
        mPhoneNumber = (EditText) findViewById(R.id.phone_field);
        mUser_reg_btn = (Button) findViewById(R.id.register_eater);
        mBackButton = (Button) findViewById(R.id.back_button);
        final Dialog dialog = new Dialog(this);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        final String username = mUsername.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String phoneNumber = mPhoneNumber.getText().toString().trim();

        if (username.isEmpty() || username.length() < 2) {
            mUsername.setError("Username must be atleast 2 characters");
        }
        else if(address.isEmpty()) {
            mAddress.setError("Choose an address");
        }
        else if(phoneNumber.isEmpty()) {

            mPhoneNumber.setError("Please fill in your phone number");
        }
         else {
            checkUserExists(username, address, phoneNumber);
        }
    }

    public void checkUserExists(final String username, final String address, final String phoneNumber) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        System.out.println(ref);
        final String checkUsername = username.toLowerCase();
        ref.orderByChild("checkusername").equalTo(checkUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ) {
                    Toast.makeText(UserReg.this, "Username already in use", Toast.LENGTH_LONG).show();
                }
                else {
                    System.out.println("Registering complete");
                    HashMap<String, String> userModel = new HashMap<>();
                    userModel.put("username", username);
                    userModel.put("checkusername", checkUsername);
                    userModel.put("phonenumber", phoneNumber);
                    userModel.put("address", address + "," + postal.getText().toString() + "," + country.getText().toString());
                    userModel.put("type", "u");
                    Intent mainIntent = new Intent(UserReg.this, UserReg2.class).putExtra("userreg", userModel);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void findPlace(View view) {
        try {
            int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("NO")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                String[] splittedAddress = place.getAddress().toString().split(",");
                if(splittedAddress.length >= 3) {
                    addressField.setText(splittedAddress[0]);
                    postal.setText(splittedAddress[1]);
                    country.setText(splittedAddress[2]);
                    mAddress.setError(null);
                }
                else {
                    Toast.makeText(this, "You have to choose street address", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}