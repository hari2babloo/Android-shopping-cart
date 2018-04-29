package com.example.jasim.plateup.registration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jasim.plateup.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RestReg2 extends AppCompatActivity {


    private static final String TAG = "Rest reg 2";
    private TextView mRestaurantAddress;
    private TextView mCityField;
    private TextView mPostalField;
    private TextView mCountry;

    private Button mAddress;

    private LinearLayout mShowAddress;

    private EditText mRestaurantTelephone;
    private EditText mBankAccount;
    private ArrayList<String> categoryArray = new ArrayList<String>();
    private ArrayList<String> spinnerArray = new ArrayList<>();
    private RecyclerView mContentList;
    private Button mNext;
    private Button mBack;
    private CheckBox mCheckBox;
    private FloatingActionButton mInfo;
    private ArrayList<String> info = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_reg2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent i = getIntent();
        info = i.getStringArrayListExtra("info");
        System.out.println(info.size());
        mShowAddress = (LinearLayout)findViewById(R.id.address_info);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");
        mAddress = (Button)findViewById(R.id.address_btn);
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(mAddress);
            }
        });
        mRestaurantAddress = (TextView) findViewById(R.id.rest_address);
        mCityField = (TextView) findViewById(R.id.city_field);
        mPostalField = (TextView) findViewById(R.id.postal_field);
        mCountry = (TextView)findViewById(R.id.country_field);
        mRestaurantTelephone = (EditText) findViewById(R.id.r_telephone);
        mBankAccount = (EditText) findViewById(R.id.bank_account);

        mInfo = (FloatingActionButton) findViewById(R.id.info_bank);
        final Dialog dialog = new Dialog(this);
        mCheckBox = (CheckBox)findViewById(R.id.checkBox);
        mNext = (Button) findViewById(R.id.next_button);
        mProgress = new ProgressDialog(this);
        mBack = (Button) findViewById(R.id.back_button);

        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.bank_info);
                dialog.show();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });


    }

    private void startRegister() {

        final String rAddress = mRestaurantAddress.getText().toString().trim();
        final String postal = mPostalField.getText().toString().trim();
        final String telephone = mRestaurantTelephone.getText().toString().trim();
        final String bankAccount = mBankAccount.getText().toString().trim();
        if(rAddress.length() == 0) {
            mRestaurantAddress.setError("Please specify an address");
        }
        else if(telephone.length() == 0) {
            mRestaurantTelephone.setError("Please specify a telephone number");
        }
        else if(bankAccount.length() == 0) {
            mBankAccount.setError("Please enter a bank account number");
        }
        else if(!mCheckBox.isChecked()) {
            mCheckBox.setError("Confirm policy");
        }
        else {
            info.add(rAddress + ", " + postal + ", " +  mCountry.getText().toString());
            info.add(telephone);
            info.add(bankAccount);
            startActivity(new Intent(RestReg2.this, RestReg3.class).putExtra("info", info));
        }
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
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
                System.out.println("Attributions: " + place.getAttributions() + place.getLocale() + place.getName());
                String[] splittedAddress = place.getAddress().toString().split(",");
                mShowAddress.setVisibility(View.VISIBLE);
                mRestaurantAddress.setText(splittedAddress[0]);
                mPostalField.setText(splittedAddress[1]);
                mCountry.setText(splittedAddress[2]);
                mAddress.setError(null);

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
