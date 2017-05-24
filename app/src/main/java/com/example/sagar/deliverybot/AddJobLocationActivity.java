package com.example.sagar.deliverybot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddJobLocationActivity extends AppCompatActivity implements JobLocationFragment.ActivityCommunicator{

    private Button mMapLocationDone, mMapLocationCancel;
    Double latitude, longitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_job_location);

        mMapLocationDone = (Button) findViewById(R.id.set_location_done);
        mMapLocationDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent();
                locationIntent.putExtra("address", address);
                locationIntent.putExtra("latitude", latitude);
                locationIntent.putExtra("longitude", longitude);
                setResult(RESULT_OK, locationIntent);
                finish();
            }
        });

        mMapLocationCancel = (Button) findViewById(R.id.set_location_cancel);
        mMapLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void updatedLatitude(Double latitude){
        this.latitude = latitude;
    }

    @Override
    public void updatedLongitude(Double longitude){
        this.longitude = longitude;
    }

    @Override
    public void updatedAddress(String address){
        this.address = address;
    }
}
