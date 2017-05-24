package com.example.sagar.deliverybot;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class JobLocationFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    public Context context;
    private Double latitude, longitude;
    private GoogleMap mMap;
    private String mParam2;
    private EditText mSearchLocation;
    private LatLng latLng;
    private String addressText;
    private ActivityCommunicator activityCommunicator;

    public interface ActivityCommunicator{
        void updatedLatitude(Double latitude);
        void updatedLongitude(Double longitude);
        void updatedAddress(String address);
    }

    public JobLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobLocationFragment newInstance(String param1, String param2) {
        JobLocationFragment fragment = new JobLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_location, container, false);
        mSearchLocation = (EditText) view.findViewById(R.id.search_location);
        mSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0){
                    String g = mSearchLocation.getText().toString();

                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> addresses = null;

                    try {
                        // Getting a maximum of 3 Address that matches the input
                        // text
                        addresses = geocoder.getFromLocationName(g, 3);
                        if (addresses != null && !addresses.equals(""))
                            search(addresses);
                        //mSearchLocation.setText("");
                    } catch (Exception e) {}
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        MapView map = (MapView) view.findViewById(R.id.add_job_map);
        map.onCreate(bundle);
        map.onResume();
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(10, 150, 10, 200);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                addMarker(point);
            }
        });

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context = getActivity();
        activityCommunicator =(ActivityCommunicator) context;
    }

    protected void search(List<Address> addresses) {
        Address address = (Address) addresses.get(0);
        latitude = address.getLongitude();
        longitude = address.getLatitude();
        latLng = new LatLng(address.getLatitude(), address.getLongitude());

        addressText = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(addressText);

        mMap.clear();
        mMap.addMarker(markerOptions);

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.animateCamera(location);
        activityCommunicator.updatedLatitude(latitude);
        activityCommunicator.updatedLongitude(longitude);
        activityCommunicator.updatedAddress(mSearchLocation.getText().toString());
    }

    public void addMarker(LatLng point){
        latitude = point.latitude;
        longitude = point.longitude;
        MarkerOptions marker = new MarkerOptions().position(point);
        mMap.clear();
        mMap.addMarker(marker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12));
        activityCommunicator.updatedLatitude(point.latitude);
        activityCommunicator.updatedLongitude(point.longitude);
        activityCommunicator.updatedAddress(mSearchLocation.getText().toString());
    }
}
