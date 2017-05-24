package com.example.sagar.deliverybot;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RootActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    View.OnClickListener mFabOnClickListener;
    private TextView mUserEmail, mUserName;
    protected FirebaseAuth mAuth;
    protected DatabaseReference firebaseDatabaseReference;
    protected FirebaseDatabase firebaseDatabase;
    protected DrawerLayout drawer;
    protected boolean isAdmin = false;
    protected ArrayList<String> driversList = new ArrayList<String>();
    protected String defaultOption;
    protected String userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        if (mAuth.getCurrentUser() != null) {
            mUserEmail = (TextView) header.findViewById(R.id.user_email);
            userEmail = mAuth.getCurrentUser().getEmail();
            mUserEmail.setText(userEmail);
            if(mAuth.getCurrentUser().getEmail().equals("admin@sdsu.edu")) {
                isAdmin = true;
                Menu menu = navigationView.getMenu();
                MenuItem nav_home = menu.findItem(R.id.nav_home);
                nav_home.setTitle(getResources().getString(R.string.nav_home_admin_title));
                MenuItem nav_itinerary = menu.findItem(R.id.nav_itinerary);
                nav_itinerary.setTitle(getResources().getString(R.string.nav_itinerary_admin_title));
            }
        }
        mUserName = (TextView) header.findViewById(R.id.user_welcome_message);
        getUsername();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_itinerary) {
            startActivity(new Intent(this, DriverItineraryActivity.class));
            //finish();
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));
            //finish();
        } else if (id == R.id.nav_signout) {
            mAuth.signOut();
            startActivity(new Intent(RootActivity.this, MainActivity.class));
            finish();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<String> getDrivers(){
        defaultOption = getResources().getString(R.string.default_option_text);
        driversList.clear();
        driversList.add(defaultOption);
        firebaseDatabaseReference.child("deliverybot").child("drivers").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> driversData;
                driversData = dataSnapshot.getChildren();
                for(DataSnapshot driver: driversData){
                    if(!driver.getKey().equals("Admin"))
                        driversList.add(driver.getKey() + " - " + driver.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return driversList;
    }

    public void getUsername(){
        firebaseDatabaseReference.child("deliverybot").child("drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userData;
                userData = dataSnapshot.getChildren();
                for(DataSnapshot user: userData){
                    String email = user.getValue().toString();
                    if(email.equals(userEmail)){
                        userName = user.getKey();
                        String welcomeMessage = getResources().getString(R.string.user_welcome_text)
                                + ", " + userName + "!";
                        mUserName.setText(welcomeMessage);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
