package com.example.neareststationfromyou;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<StationInfo> stationInfos;

    public MyAdapter(Context context, ArrayList<StationInfo> stationInfos) {
        this.context = context;
        this.stationInfos = stationInfos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.address.setText(stationInfos.get(i).getAdd());
        myViewHolder.city.setText(stationInfos.get(i).getCity());

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ViewDetails.class);
                intent.putExtra("key",stationInfos.get(i).getUploadId());
                intent.putExtra("lat",stationInfos.get(i).getLat());
                intent.putExtra("lon",stationInfos.get(i).getLon());
                intent.putExtra("address",stationInfos.get(i).getAdd());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stationInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView address,city;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            address=itemView.findViewById(R.id.address);
            city=itemView.findViewById(R.id.city);
            cardView=itemView.findViewById(R.id.cardView_id);
        }

    }
}
