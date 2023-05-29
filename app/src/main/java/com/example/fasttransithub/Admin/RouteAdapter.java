package com.example.fasttransithub.Admin;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    private ArrayList<Route> routeList;


    public RouteAdapter(ArrayList<Route> routeList) {
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.M_Name.setText(route.getName());
        holder.M_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //load new activity here
            }
        });
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG",route.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView M_Name;
        public View delete_button;
        public ViewHolder(@NonNull View v) {
            super(v);
            M_Name = v.findViewById(R.id.routeName);
            delete_button = v.findViewById(R.id.deleteButton);
        }
    }
}
