package com.example.neelmani.fukrey;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static  String showLocation;
    private TextView tvSwitchHideLocationStatus;
    private Switch switchHideLocation;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvSwitchHideLocationStatus = (TextView) findViewById(R.id.switchHideLocationStatus);
        switchHideLocation = (Switch) findViewById(R.id.switchHideLocation);

        showLocation=MainActivity.getSharedPreferences().getString(MainActivity.PREF_SHOW_LOCATION, "");

        if(showLocation.equals("True"))
            switchHideLocation.setChecked(true);
        else
            switchHideLocation.setChecked(false);

        //attach a listener to check for changes in state
        switchHideLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
               String status;
                if(isChecked){
                    status="True";
                    tvSwitchHideLocationStatus.setText("Your location will be visible in your posts");
                }else{
                   status="False";
                    tvSwitchHideLocationStatus.setText("Your location will be hidden in your posts");
                }
                SharedPreferences.Editor editor = MainActivity.getSharedPreferences().edit();
                editor.putString(MainActivity.PREF_SHOW_LOCATION, status);
                editor.apply();
            }
        });

        //check the current state before we display the screen
        if(switchHideLocation.isChecked()){
            tvSwitchHideLocationStatus.setText("Your location will be visible in your posts");
        }
        else {
            tvSwitchHideLocationStatus.setText("Your location will be hidden in your posts");
        }
    }




    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart SettingsActivity fired ..............");
        }

     @Override
     public void onResume() {
        super.onResume();
        Log.d(TAG, "SettingsActivity Resumed.....................");
        }

      @Override
      protected void onPause() {
        super.onPause();
        }

      @Override
      public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop SettingsActivity fired ..............");
        }

        }

