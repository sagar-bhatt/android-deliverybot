package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddJobActivity extends RootActivity {

    private final int LOCATION_INTENT_REQUEST_CODE = 123;
    private Button mMapLocationDone, mMapLocationCancel, mJobLocationSet;
    private TextView mJobLocation;
    private Double latitude, longitude;
    private String address = "", selectedDriver, defaultOption;
    private EditText mJobId, mShipTo, mShipperContact, mPaymentDue;
    private Spinner mJobAssignTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_job);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_add_job, null, false);
        drawer.addView(contentView, 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        mJobLocation = (TextView) findViewById(R.id.job_location);
        mJobAssignTo = (Spinner) findViewById(R.id.job_assign_to);
        showDrivers();

        mJobLocationSet = (Button) findViewById(R.id.set_job_location);
        mJobLocationSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationIntent = new Intent(AddJobActivity.this, AddJobLocationActivity.class);
                startActivityForResult(locationIntent, LOCATION_INTENT_REQUEST_CODE);
            }
        });

        mMapLocationDone = (Button) findViewById(R.id.add_job_done);
        mMapLocationDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toastMessage = "";
                try {
                    addJob();
                }catch(Exception e){
                    toastMessage = getResources().getString(R.string.job_creation_error_toast);
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        mMapLocationCancel = (Button) findViewById(R.id.add_job_cancel);
        mMapLocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_INTENT_REQUEST_CODE && resultCode == RESULT_OK){
            address = data.getStringExtra("address");
            latitude = data.getDoubleExtra("latitude", 0.0);
            longitude = data.getDoubleExtra("longitude", 0.0);
            if(address == null || address.isEmpty()) {
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String addressLine = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    address = addressLine + " " +
                            city + " " +
                            state + " " +
                            country + " " +
                            postalCode;
                    mJobLocation.setText(address);
                    //mJobLocation.setText(String.format("%.6f", latitude) + ", " + String.format("%.6f", longitude));
                }catch (Exception e){
                }
            }else
                mJobLocation.setText(address);
        }
    }

    public void addJob(){
        mJobId = (EditText) findViewById(R.id.job_id);
        mShipTo = (EditText) findViewById(R.id.ship_to);
        mShipperContact = (EditText) findViewById(R.id.ship_contact);
        mPaymentDue = (EditText) findViewById(R.id.payment_due);

        final String jobId = mJobId.getText().toString();
        final String shipTo = mShipTo.getText().toString();
        final String shipperContact = mShipperContact.getText().toString();
        String paymentDue = mPaymentDue.getText().toString();

        boolean isValid = true;
        String toastMessage = "";

        if(selectedDriver.equals(defaultOption) || selectedDriver.isEmpty()){
            toastMessage = getResources().getString(R.string.driver_error);
            isValid = false;
        }
        if(address.equals("") || latitude == 0.0 || longitude == 0.0){
            toastMessage = getResources().getString(R.string.address_error);
            isValid = false;
        }
        if(shipperContact.equals("")){
            toastMessage = getResources().getString(R.string.shipper_contact_error);
            isValid = false;
        }
        if(shipTo.equals("")){
            toastMessage = getResources().getString(R.string.ship_to_error);
            isValid = false;
        }
        if(jobId.equals("")){
            toastMessage = getResources().getString(R.string.job_id_error);
            isValid = false;
        }
        if(paymentDue.equals("")){
            paymentDue = String.valueOf(0.0);
        }
        final String payment = paymentDue;

        if(isValid) {
            firebaseDatabaseReference.child("deliverybot").child("jobs").orderByKey().equalTo(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChildren())
                        addNewJob(jobId, shipTo, shipperContact, payment);
                    else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.order_id_exists), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }else{
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void addNewJob(String jobId, String shipTo, String shipperContact, String paymentDue){
        // Add job information to firebase
        JobInfo job = new JobInfo(shipTo,
                shipperContact,
                paymentDue,
                address,
                String.valueOf(latitude),
                String.valueOf(longitude));
        Map<String, Object> postValues = job.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("deliverybot/jobs/" + jobId + "/info", postValues);
        firebaseDatabaseReference.updateChildren(childUpdates);

        if (selectedDriver.isEmpty())
            firebaseDatabaseReference.child("deliverybot/jobs/" + jobId + "/status").setValue("Unassigned");
        else
            firebaseDatabaseReference.child("deliverybot/jobs/" + jobId + "/status").setValue("Assigned");
        firebaseDatabaseReference.child("deliverybot/jobs/" + jobId + "/driver").setValue(selectedDriver);

        // Add route information to firebase
        Route route = new Route(address,
                String.valueOf(latitude),
                String.valueOf(longitude),
                selectedDriver);
        Map<String, Object> postRouteUpdate = route.toMap();
        Map<String, Object> routeChildUpdates = new HashMap<>();
        routeChildUpdates.put("deliverybot/routes/" + jobId, postRouteUpdate);
        firebaseDatabaseReference.updateChildren(routeChildUpdates);
        String toastMessage = getResources().getString(R.string.job_creation_toast);
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    public void showDrivers(){
        ArrayList<String> drivers = getDrivers();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, drivers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJobAssignTo.setAdapter(adapter);
        mJobAssignTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    selectedDriver = mJobAssignTo.getSelectedItem().toString();
                    selectedDriver = selectedDriver.substring(selectedDriver.lastIndexOf(" ")+1);
                }
                else
                    selectedDriver = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
