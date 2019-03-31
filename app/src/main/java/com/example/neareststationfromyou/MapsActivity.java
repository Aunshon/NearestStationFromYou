package com.example.neareststationfromyou;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    public static final int MY_PERMISSTION_REQUESR_CODE=1996;
    public static final int PLAY_SERVICE_RESOLUTION_REQUEST=890;
    private GoogleApiClient mgoogleApiClient;
    private Location lastlocation;
    private LocationRequest mlocationRequest;
    private LocationCallback locationCallback;
    private int UPDATE_INTERVAL=5000;
    private int FASTEST_INTERVAL=3000;
    private int Displacement=10;
    Marker currentmarker;
    DatabaseReference mdatabaseRef;
    LatLng latLngtoconvert ;
    Geocoder geocoder;
    String add = null;
    String city = null;
    String state= null;
    String country= null;
    String postalCode= null;
    String knownName= null;
    StationInfo stationInfo;
    Marker marker,s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //mSearchText=findViewById(R.id.input_search);

        mdatabaseRef=FirebaseDatabase.getInstance().getReference("AllStations");
        setUpLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSTION_REQUESR_CODE:
                if (grantResults.length<0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if (checkPlayservice()){
                        builtGoogleApiCliect();
                        createLocatinoRequest();
                        DisplayLocation();
                    }
                }
                break;
        }
    }
    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //Request runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSTION_REQUESR_CODE);
        }
        else{
            if (checkPlayservice()){
                builtGoogleApiCliect();
                createLocatinoRequest();
                DisplayLocation();
            }
        }
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
//        locationCallback=new LocationCallback();
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient,mlocationRequest,this);
//        fusedLocationProviderClient.requestLocationUpdates(mlocationRequest,locationCallback,null);
    }
    private void createLocatinoRequest() {
        mlocationRequest=new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(Displacement);
    }
    private void builtGoogleApiCliect() {
        mgoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mgoogleApiClient.connect();
    }
    private boolean checkPlayservice() {
        int resultcode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode!= ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)){
                GooglePlayServicesUtil.getErrorDialog(resultcode,this,PLAY_SERVICE_RESOLUTION_REQUEST).show();
            }
            else {
                Toast.makeText(this, "This Device Is Not Supported 😭", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }
    private void DisplayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        lastlocation= LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (lastlocation!=null){
            final double latitude=lastlocation.getLatitude();
            final double longitude=lastlocation.getLongitude();
            if (currentmarker!=null){
                currentmarker.remove();
                //currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Your Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);

            }
            else {
                //currentmarker=mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("you"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));

            }
            Log.d("EDMTDEV",String.format("Your location was changed : %f / %f ",latitude,longitude));

        }
        else {
            Log.d("EDMTDEV","Can not get Location");
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(23.7545861, 90.3752816);
        currentmarker=mMap.addMarker(new MarkerOptions().position(sydney).title("Daffodil International University"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.7545861, 90.3752816), 12.0f));
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        latLngtoconvert = mMap.getCameraPosition().target;

        mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int a=0;
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    //Toast.makeText(MapsActivity.this, a, Toast.LENGTH_SHORT).show();
                    StationInfo station=dataSnapshot1.getValue(StationInfo.class);
                    LatLng sydney = new LatLng(station.getLat(), station.getLon());
                    marker=mMap.addMarker(new MarkerOptions().position(sydney).title(station.getAdd()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DisplayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation=location;
        DisplayLocation();
    }


}
