package com.example.neareststationfromyou;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mdatabaseRef;
    RecyclerView recyclerView;
    ArrayList<StationInfo> list;
    MyAdapter myAdapter, newAdapter;
    SearchView searchView;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat,currentLon;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scarch, menu);
        final MenuItem menuItem = menu.findItem(R.id.scarch);
        searchView = (SearchView) menuItem.getActionView();
        changeSearchViewTextColor(searchView);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.white));
        searchView.setMaxWidth(700);
        searchView.setQueryHint("Search Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<StationInfo> filtermodelist = (ArrayList<StationInfo>) filtered(list, newText);
                newAdapter = new MyAdapter(MainActivity.this, filtermodelist);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(newAdapter);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //for changing the text color of searchview
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private List<StationInfo> filtered(List<StationInfo> p1, String query) {
        query = query.toLowerCase();
        List<StationInfo> filteredArrayList = new ArrayList<>();
        for (StationInfo model : p1) {
            final String text = model.getAdd().toLowerCase();
            if (text.startsWith(query)) {
                filteredArrayList.add(model);
            }
        }
        return filteredArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location !=null){
                    currentLat=location.getLatitude();
                    currentLon=location.getLongitude();
                }
            }
        });



        list = new ArrayList<StationInfo>();
        mdatabaseRef = FirebaseDatabase.getInstance().getReference().child("AllStations");
        mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    StationInfo station = dataSnapshot1.getValue(StationInfo.class);
                    list.add(station);

//                    float result[] = new float[100];
//                    Location.distanceBetween(currentLat,currentLon,station.lat,station.lon,result);
//                    if (result[0] <= 1000){
////                        Toast.makeText(MainActivity.this, ""+result[0], Toast.LENGTH_SHORT).show();
//                        list.add(station);
//                    }
                }
                myAdapter = new MyAdapter(MainActivity.this, list);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setClickable(true);









            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Go(View view) {
        startActivity(new Intent(MainActivity.this,MapsActivity.class));
    }

    public void BusGo(View view) {
        startActivity(new Intent(MainActivity.this,bus_search.class));
    }

    public void CityGo(View view) {
        startActivity(new Intent(MainActivity.this,CitySearch.class));
    }
}
