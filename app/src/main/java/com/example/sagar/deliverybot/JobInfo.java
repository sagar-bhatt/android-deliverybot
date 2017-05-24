package com.example.sagar.deliverybot;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 5/13/2017.
 */

public class JobInfo {

    public String jobId;
    public String shipTo;
    public String shipperContact;
    public String paymentDue;
    public String address;
    public String latitude;
    public String longitude;
    public String driver;
    public String status;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }

    public String getShipperContact() {
        return shipperContact;
    }

    public void setShipperContact(String shipperContact) {
        this.shipperContact = shipperContact;
    }

    public String getPaymentDue() {
        return paymentDue;
    }

    public void setPaymentDue(String paymentDue) {
        this.paymentDue = paymentDue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JobInfo(){}

    public JobInfo(String jobId,
                   String shipTo,
                   String driver,
                   String status) {
        super();
        this.jobId= jobId;
        this.shipTo= shipTo;
        this.driver = driver;
        this.status = status;
    }

    public JobInfo(String shipTo,
                   String shipperContact,
                   String paymentDue,
                   String address,
                   String latitude,
                   String longitude) {
        super();
        this.shipTo= shipTo;
        this.shipperContact = shipperContact;
        this.paymentDue = paymentDue;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ship_to", this.shipTo);
        result.put("shipper_contact", this.shipperContact);
        result.put("address", this.address);
        result.put("latitude", this.latitude);
        result.put("longitude", this.longitude);
        result.put("payment_due", this.paymentDue);

        return result;
    }
}
