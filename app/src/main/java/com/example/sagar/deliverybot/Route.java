package com.example.sagar.deliverybot;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar on 5/13/2017.
 */

public class Route {
    public String address;
    public String latitude;
    public String longitude;
    public String driver;

    public Route(){}

    public Route(String address,
                   String latitude,
                   String longitude,
                   String driver) {
        super();
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.driver = driver;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("address", this.address);
        result.put("latitude", this.latitude);
        result.put("longitude", this.longitude);
        result.put("driver", this.driver);

        return result;
    }
}
