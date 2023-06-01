package com.example.fasttransithub.Admin;



import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    private ArrayList<Route> routeList;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public RouteAdapter(ArrayList<Route> routeList) {
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Routes");
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
        DatabaseReference databaseReference2 = databaseReference.child(route.getName());
        holder.M_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RouteDataActivity.class);
                intent.putExtra("route", route.getName());
                v.getContext().startActivity(intent);
            }
        });
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference2.removeValue();
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
