package com.example.neareststationfromyou;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder> {
    Context context;
    ArrayList<StationDetails> stationDetails;

    public DetailsAdapter(Context context, ArrayList<StationDetails> stationDetails) {
        this.context = context;
        this.stationDetails = stationDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DetailsAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.dcard,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.d.setText(stationDetails.get(i).getD());
        myViewHolder.t.setText(stationDetails.get(i).getT());
        myViewHolder.v.setText(stationDetails.get(i).getV());
    }

    @Override
    public int getItemCount() {
        return stationDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView v,t,d;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            v=itemView.findViewById(R.id.v);
            t=itemView.findViewById(R.id.t);
            d=itemView.findViewById(R.id.d);
        }

    }
}
