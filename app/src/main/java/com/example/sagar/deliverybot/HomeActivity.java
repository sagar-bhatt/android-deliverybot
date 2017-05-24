package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends RootActivity {

    private ListView mListView;
    private Spinner mDrivers;
    private String selectedDriver;
    private JobsListViewAdapter mJobListViewAdapter;
    private ArrayList<JobInfo> jobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_home, null, false);
        drawer.addView(contentView, 0);
        if(!isAdmin) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.hide();
        }

        FrameLayout contentHome = (FrameLayout) findViewById(R.id.list_holder);
        mListView = (ListView) contentHome.findViewById(R.id.list);
        getJobs();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a new job", Snackbar.LENGTH_LONG)
                        .setAction("Action", mFabOnClickListener).show();
            }
        });

        mFabOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(HomeActivity.this, AddJobActivity.class));
            }
        };
    }

    public void getJobs(){
        jobs = new ArrayList<JobInfo>();
        DatabaseReference ref = firebaseDatabase.getReference("deliverybot/jobs");
        Query query;
        //ref.orderByKey().addValueEventListener(new ValueEventListener() {

        if(isAdmin)
            query = ref.orderByKey();
        else
            query = ref.orderByChild("driver").equalTo(userEmail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> jobsData;
                jobsData = dataSnapshot.getChildren();
                jobs.clear();
                for(DataSnapshot job: jobsData){
                    String shipTo = (String) job.child("info").child("ship_to").getValue();
                    String driver = (String) job.child("driver").getValue();
                    String status = (String) job.child("status").getValue();
                    jobs.add(new JobInfo(job.getKey(), shipTo, driver, status));
                }
                if(jobs.size() == 0){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_jobs_found_text), Toast.LENGTH_LONG).show();
                }else {
                    JobsListViewAdapter adapter = new JobsListViewAdapter(jobs, HomeActivity.this);
                    mListView.setAdapter(adapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(HomeActivity.this, JobDetailActivity.class);
                            String jobId = ((TextView) view.findViewById(R.id.job_id)).getText().toString();
                            intent.putExtra("job_id", jobId);
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
