package com.example.neareststationfromyou;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class bus_search extends AppCompatActivity {
    DatabaseReference mdatabaseRef;
    RecyclerView recyclerView;
    ArrayList<StationInfo> list;
    ArrayList<StationDetails> list1;
    MyAdapter myAdapter,newAdapter;
    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scarch,menu);
        final MenuItem menuItem=menu.findItem(R.id.scarch);
        searchView= (SearchView) menuItem.getActionView();
        changeSearchViewTextColor(searchView);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(getResources().getColor(R.color.white));
        searchView.setMaxWidth(700);
        searchView.setQueryHint("Search Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!searchView.isIconified()){
                    searchView.setIconified(true);
                }
                menuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<StationInfo> filtermodelist= (ArrayList<StationInfo>) filtered(list,newText);
                newAdapter=new MyAdapter(bus_search.this,filtermodelist);
                recyclerView.setLayoutManager(new LinearLayoutManager(bus_search.this));
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
    private List<StationInfo> filtered(List<StationInfo>p1, String query){
        query= query.toLowerCase();
        final List<StationInfo>filteredArrayList=new ArrayList<>();
        for(final StationInfo model:p1){
            mdatabaseRef= FirebaseDatabase.getInstance().getReference().child("vehicles").child(model.getUploadId());
            final String finalQuery = query;
            mdatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        StationDetails station=dataSnapshot1.getValue(StationDetails.class);
//                        String a=station.getV();
//                        final String text=model.getAdd().toLowerCase();
                        String text=station.getV().toLowerCase();
                        if(text.startsWith(finalQuery)){
                            filteredArrayList.add(model);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return filteredArrayList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search);


        recyclerView=findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<StationInfo>();
        list1=new ArrayList<StationDetails>();
        mdatabaseRef= FirebaseDatabase.getInstance().getReference().child("AllStations");
        mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    StationInfo station=dataSnapshot1.getValue(StationInfo.class);
                    list.add(station);
                }
                myAdapter=new MyAdapter(bus_search.this,list);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setClickable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(bus_search.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
