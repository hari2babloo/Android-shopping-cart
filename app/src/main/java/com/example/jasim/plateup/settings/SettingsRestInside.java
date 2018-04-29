package com.example.jasim.plateup.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsRestInside extends AppCompatActivity {
    private static final String TAG = "SettingsRestINside";
    private Button mBack;
    private Spinner sItems;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ArrayList<String> spinnerArray = new ArrayList<>();
    private EditText rName;
    private TextView rAddress;
    private EditText rTelephone;
    private RestaurantModel model;

    private Button mChangeAddress;
    private Button rSave;


    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Contentpictures");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(SettingsRestInside.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_rest_inside);
        rName = (EditText)findViewById(R.id.r_name_field);
        rAddress = (TextView)findViewById(R.id.r_address);
        rTelephone = (EditText)findViewById(R.id.r_telephone);
        mChangeAddress = (Button)findViewById(R.id.change_address);
        mChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(mChangeAddress);
            }
        });
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(RestaurantModel.class);
                sItems.setSelection(spinnerArray.indexOf(model.getCategory()));
                rName.setText(model.getUsername());
                rAddress.setText(model.getAddress());
                rTelephone.setText(model.getTelephone());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Logic for failure is here...
            }
        });

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


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.category_field);
        sItems.setAdapter(adapter);
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
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        model.setUsername(rName.getText().toString());
                        model.setAddress(rAddress.getText().toString());
                        model.setCategory(sItems.getSelectedItem().toString());
                        dataSnapshot.getRef().setValue(model);
                        onBackPressed();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }
    public void findPlace(View view) {

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("NO")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        try {
            int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
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
                rAddress.setText(place.getAddress().toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


}
