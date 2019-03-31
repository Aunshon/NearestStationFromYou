package com.example.neareststationfromyou;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDetails extends AppCompatActivity {
    TextView addressof;
    DatabaseReference mdatabaseRef;
    RecyclerView recyclerView;
    ArrayList<StationDetails> list;
    DetailsAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        addressof=findViewById(R.id.addressof);

        Intent intent=getIntent();
        addressof.setText(intent.getExtras().getString("address"));

        recyclerView=findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<StationDetails>();
        mdatabaseRef= FirebaseDatabase.getInstance().getReference().child("vehicles").child(intent.getExtras().getString("key"));
        mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    StationDetails station=dataSnapshot1.getValue(StationDetails.class);
                    list.add(station);
                }
                myAdapter=new DetailsAdapter(ViewDetails.this,list);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
