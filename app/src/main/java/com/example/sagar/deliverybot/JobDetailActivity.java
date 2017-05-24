package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JobDetailActivity extends RootActivity {

    private TextView mJobId, mShipTo, mShipperContact, mPaymentDue, mShipperAddress, mJobDriver;
    private String jobId;
    private Button mJobDelivered, mJobCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_job_detail);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_job_detail, null, false);
        drawer.addView(contentView, 0);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("job_id");
        fetchElements();
        getJobDetails();

        mJobDelivered = (Button) findViewById(R.id.job_delivered);
        mJobCancelled = (Button) findViewById(R.id.job_cancelled);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(isAdmin) {
            mJobDelivered.setVisibility(View.GONE);
            mJobCancelled.setVisibility(View.GONE);
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
                    startActivity(new Intent(JobDetailActivity.this, AddJobActivity.class));
                    finish();
                }
            };
        }else {
            fab.hide();

            mJobDelivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent eSignIntent = new Intent(JobDetailActivity.this, ESignActivity.class);
                    eSignIntent.putExtra("job_id", jobId);
                    startActivity(eSignIntent);
                    finish();
                }
            });

            mJobCancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    firebaseDatabaseReference.child("deliverybot").child("jobs").child(jobId).child("status").setValue("Canceled");
                }
            });
        }
    }

    public void onClick(View v) {
        TextView contactInfo = (TextView) v.findViewById(R.id.ship_contact);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contactInfo.getText().toString()));
        startActivity(intent);
    }

    public void getJobDetails(){
        DatabaseReference ref = firebaseDatabase.getReference("deliverybot/jobs");

        ref.orderByKey().equalTo(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> routeData;
                routeData = dataSnapshot.getChildren();
                for(DataSnapshot route: routeData){
                    String shipTo = (String) route.child("info").child("ship_to").getValue();
                    String shipToAddress = (String) route.child("info").child("address").getValue();
                    String shipperContact = (String) route.child("info").child("shipper_contact").getValue();
                    String paymentDue = (String) route.child("info").child("payment_due").getValue();
                    String status = (String) route.child("status").getValue();
                    String driver = (String) route.child("driver").getValue();
                    firebaseDatabaseReference.child("deliverybot").child("drivers").orderByValue().equalTo(driver).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> driversData;
                            driversData = dataSnapshot.getChildren();
                            for(DataSnapshot driverInfo: driversData){
                                mJobDriver.setText(driverInfo.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    mJobId.setText(jobId);
                    mShipTo.setText(shipTo);
                    mShipperAddress.setText(shipToAddress);
                    mPaymentDue.setText(paymentDue);
                    SpannableString content = new SpannableString(shipperContact);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    mShipperContact.setText(content);
                    if(status.equals("Delivered") || status.equals("Canceled"))
                        hideJobControls();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void fetchElements(){
        mJobId = (TextView) findViewById(R.id.job_id);
        mShipTo = (TextView) findViewById(R.id.ship_to);
        mShipperAddress = (TextView) findViewById(R.id.shipper_address);
        mShipperContact = (TextView) findViewById(R.id.ship_contact);
        mPaymentDue = (TextView) findViewById(R.id.payment_due);
        mJobDriver = (TextView) findViewById(R.id.job_driver);
    }

    public void hideJobControls(){
        mJobDelivered.setVisibility(View.GONE);
        mJobCancelled.setVisibility(View.GONE);
    }
}
