package com.example.sagar.deliverybot;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverItineraryFragment extends Fragment implements OnMapReadyCallback {

    //private final String serverKey = "AIzaSyBGJWjSd_El3y0Fq4Fa-ieHWPpVV9V39MA";
    private String serverKey;
    public Context context;
    private Double latitude, longitude;
    private GoogleMap mMap;
    private LatLng latLng;
    private String email;
    private boolean isAdminUser;
    private ArrayList<Integer> mapColors = new ArrayList<Integer>();

    public DriverItineraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_itinerary, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        MapView map = (MapView) view.findViewById(R.id.driver_itinerary_map);
        map.onCreate(bundle);
        map.onResume();
        map.getMapAsync(this);

        DriverItineraryActivity driverItinerary = (DriverItineraryActivity) getActivity();
        serverKey = driverItinerary.getGoogleDirectionsApiKey();
        if(serverKey.isEmpty() || serverKey.equals(""))
            Toast.makeText(
                    getContext(),
                    getResources().getString(R.string.directions_key_error), Toast.LENGTH_SHORT).show();
        email = driverItinerary.getUserEmail();
        isAdminUser = driverItinerary.getIsAdmin();
        if(isAdminUser) {
            mapColors.add(Color.RED);
            mapColors.add(Color.GREEN);
            mapColors.add(Color.DKGRAY);
            mapColors.add(Color.LTGRAY);
            mapColors.add(Color.BLACK);
            mapColors.add(Color.BLUE);
            //allDrivers = driverItinerary.getAllDrivers();
        }

        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || true) {
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if(location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(10, 150, 10, 200);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //addMarker(point);
            }
        });

        final boolean adminUser = isAdminUser;

        if(adminUser){
            DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseDatabaseReference.child("deliverybot").child("drivers").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> driversData;
                    driversData = dataSnapshot.getChildren();
                    for(DataSnapshot driver: driversData){
                        int min = 0;
                        int max = mapColors.size();
                        Random random = new Random();
                        int value = random.nextInt(max - min) + min;
                        final Integer routeColor = mapColors.get(value);
                        mapColors.remove(value);
                        if(!driver.getKey().equals("Admin")){
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference ref = firebaseDatabase.getReference("deliverybot/routes");

                            ref.orderByChild("driver").equalTo(driver.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot routeDataSnapshot) {
                                    Iterable<DataSnapshot> routesData;
                                    routesData = routeDataSnapshot.getChildren();
                                    boolean firstLocation = true;
                                    Double routeSourceLat = 0.0;
                                    Double routeSourceLong = 0.0;
                                    Double routeDestLat = 0.0;
                                    Double routeDestLong = 0.0;
                                    String sourceJob, destJob = "";
                                    for (DataSnapshot route : routesData) {
                                        routeSourceLat = routeDestLat;
                                        routeSourceLong = routeDestLong;
                                        sourceJob = destJob;
                                        routeDestLat = Double.parseDouble((String) route.child("latitude").getValue());
                                        routeDestLong = Double.parseDouble((String) route.child("longitude").getValue());
                                        destJob = route.getKey();
                                        if(!firstLocation) {
                                            showItinerary(new LatLng(routeSourceLat, routeSourceLong), sourceJob,
                                                    new LatLng(routeDestLat, routeDestLong), destJob, routeColor);
                                        }
                                        else
                                            firstLocation = false;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        else {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference ref = firebaseDatabase.getReference("deliverybot/routes");
            ref.orderByChild("driver").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> routesData;
                    routesData = dataSnapshot.getChildren();
                    boolean initialLocation = false;
                    Double routeSourceLat = 0.0;
                    Double routeSourceLong = 0.0;
                    Double routeDestLat = 0.0;
                    Double routeDestLong = 0.0;
                    String sourceJob, destJob = "";
                    for (DataSnapshot route : routesData) {
                        if (!initialLocation) {
                            routeSourceLat = latitude;
                            routeSourceLong = longitude;
                            sourceJob = getResources().getString(R.string.my_location);
                            initialLocation = true;
                        } else {
                            routeSourceLat = routeDestLat;
                            routeSourceLong = routeDestLong;
                            sourceJob = destJob;
                        }
                        routeDestLat = Double.parseDouble((String) route.child("latitude").getValue());
                        routeDestLong = Double.parseDouble((String) route.child("longitude").getValue());
                        destJob = route.getKey();
                        showItinerary(new LatLng(routeSourceLat, routeSourceLong), sourceJob,
                                new LatLng(routeDestLat, routeDestLong), destJob, Color.RED);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    public void showItinerary(final LatLng origin, final String sourceJobTitle,
                              final LatLng destination, final String destJobTitle, final Integer routeColor){
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        if(direction.isOK()) {
                            try{
                                List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(), stepList, 5, routeColor, 3, Color.BLUE);
                                for (PolylineOptions polylineOption : polylineOptionList) {
                                    mMap.addPolyline(polylineOption);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(
                                    getContext(),
                                    getResources().getString(R.string.location_fetch_error_text), Toast.LENGTH_SHORT).show();
                        }
                        MarkerOptions marker = new MarkerOptions().position(origin).title(sourceJobTitle);
                        mMap.addMarker(marker).showInfoWindow();
                        marker = new MarkerOptions().position(destination).title(destJobTitle);
                        mMap.addMarker(marker).showInfoWindow();
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
    }

    public void addMarker(LatLng point){
        latitude = point.latitude;
        longitude = point.longitude;
        MarkerOptions marker = new MarkerOptions().position(point);
        mMap.clear();
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12));
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(
                    getContext(),
                    "Current location: " + loc.getLatitude() + ", "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
