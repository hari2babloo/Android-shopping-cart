package com.example.jasim.plateup.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.R;

public class SettingsUser extends AppCompatActivity {
    private TextView settingsRestaurant;
    private TextView settingsPassword;
    private Button mBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings_user);


        mBack = (Button) findViewById(R.id.back_button);
        settingsRestaurant = (TextView)findViewById(R.id.user_settings);
        settingsPassword = (TextView)findViewById(R.id.user_password);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsUser.this, MainUActivity.class));
            }
        });
        settingsRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsUser.this, SettingsUserProfile.class));
            }
        });

        settingsPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsUser.this, SettingsUserInside.class));
            }
        });

    }
}
