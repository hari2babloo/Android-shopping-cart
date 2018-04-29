package com.example.jasim.plateup.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.R;

public class SettingsRest extends AppCompatActivity {
    private TextView settingsRestaurant;
    private TextView settingsPassword;
    private TextView settingsBank;
    private TextView settingsOpening;
    private Button mBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_rest);


        mBack = (Button) findViewById(R.id.back_button);
        settingsRestaurant = (TextView)findViewById(R.id.rest_settings);
        settingsPassword = (TextView)findViewById(R.id.rest_password);
        settingsBank = (TextView)findViewById(R.id.rest_bank);
        settingsOpening = (TextView)findViewById(R.id.rest_profile);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsRest.this, MainRActivity.class));
            }
        });
        settingsRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsRest.this, SettingsRestInside.class));
            }
        });

        settingsPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsRest.this, SettingsRestPass.class));
            }
        });

        settingsBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsRest.this, SettingsRestBank.class));
            }
        });

        settingsOpening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsRest.this, SettingsRestOpening.class));
            }
        });

    }
}
