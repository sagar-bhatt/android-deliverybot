package com.example.sagar.deliverybot;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 5/13/2017.
 */

public class JobsListViewAdapter extends ArrayAdapter<JobInfo> implements View.OnClickListener {
    public List<JobInfo> jobsList;
    private Context mContext;
    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView mJobId, mJobInfo;
    }

    public JobsListViewAdapter(ArrayList<JobInfo> data, Context context) {
        super(context, R.layout.job_list_info, data);
        this.jobsList = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the users item for this position
        JobInfo job = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.job_list_info, parent, false);
            viewHolder.mJobId = (TextView) convertView.findViewById(R.id.job_id);
            viewHolder.mJobInfo = (TextView) convertView.findViewById(R.id.job_info);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        String shipToLabel = getContext().getResources().getString(R.string.ship_to_label);
        String driverLabel = getContext().getResources().getString(R.string.driver_label);
        String statusLabel = getContext().getResources().getString(R.string.status_label);
        String jobData = shipToLabel + " : " + job.getShipTo() + System.getProperty("line.separator");
        if(!job.getDriver().isEmpty()) {
            jobData = jobData + driverLabel + " : " + job.getDriver() + System.getProperty("line.separator");
        }
        jobData = jobData + statusLabel + " : " + job.getStatus();
        viewHolder.mJobId.setText(job.getJobId());
        viewHolder.mJobInfo.setText(jobData);
        if(job.getStatus().equals("Delivered"))
            convertView.setBackgroundColor(Color.parseColor("#AED581"));
        else if(job.getStatus().equals("Canceled"))
            convertView.setBackgroundColor(Color.parseColor("#EF9A9A"));
        else
            convertView.setBackgroundColor(Color.LTGRAY);
        return convertView;
    }
}
