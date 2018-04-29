package com.example.jasim.plateup;

import android.*;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.enums.EPickType;

/**
 * Created by jrvansuita build 30/01/17.
 */

public abstract class BaseSampleActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);

        imageView = findViewById(R.id.result_image);

        initialize();
    }


    public static class InnerPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            UI.setSummaryForGroups((PreferenceGroup) findPreference(getString(R.string.key_text_preference_group)));
            UI.setSummaryForGroups((PreferenceGroup) findPreference(getString(R.string.key_additional_preference_group)));

            UI.setDynamicHeight((ListView) getView().findViewById(android.R.id.list));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            UI.setSummary(sharedPreferences, findPreference(key));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        scrollToTop();*/
    }

    /* public void scrollToTop() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                scrollView.fullScroll(scrollView.FOCUS_UP);
            }
        }, 50);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(scrollView.FOCUS_UP);
            }
        });
    } */


    protected void initialize() {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                imageView.setImageResource(R.mipmap.default_image);
                return true;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(BaseSampleActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(BaseSampleActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(BaseSampleActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(BaseSampleActivity.this,
                                new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                onImageViewClick();
            }
        });
    }

    public ImageView getImageView() {
        return imageView;
    }

    protected abstract void onImageViewClick();
}