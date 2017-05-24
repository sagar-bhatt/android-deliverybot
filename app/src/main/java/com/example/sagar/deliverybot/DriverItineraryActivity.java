package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

public class DriverItineraryActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_driver_itinerary);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_driver_itinerary, null, false);
        drawer.addView(contentView, 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(isAdmin) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Add a new job", Snackbar.LENGTH_LONG)
                            .setAction("Action", mFabOnClickListener).show();
                }
            });

            mFabOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DriverItineraryActivity.this, AddJobActivity.class));
                    finish();
                }
            };
        }else
            fab.hide();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public ArrayList<String> getAllDrivers(){
        ArrayList<String> drivers = getDrivers();
        return drivers;
    }

    public String getGoogleDirectionsApiKey(){
        String directionsApiKey;
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            directionsApiKey = bundle.getString("DIRECTIONS_API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            directionsApiKey = "";
        } catch (NullPointerException e) {
            directionsApiKey = "";
        }
        return directionsApiKey;
    }
}
