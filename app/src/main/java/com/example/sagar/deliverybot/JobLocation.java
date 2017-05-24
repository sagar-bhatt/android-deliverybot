package com.example.sagar.deliverybot;

/**
 * Created by Sagar on 5/14/2017.
 */

public class JobLocation {
    public String jobId;
    public String latitude;
    public String longitude;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public JobLocation(){}

    public JobLocation(String jobId, String latitude, String longitude){
        this.jobId = jobId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
