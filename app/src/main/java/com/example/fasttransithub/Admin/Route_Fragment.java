package com.example.fasttransithub.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.fasttransithub.Authentication.UserSignupActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Route_Fragment extends Fragment {
    View view;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    public Route_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Routes");

        getRoutes();
    }

    private void getRoutes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Route> routes = new ArrayList();
                try {
                    snapshot.getChildren().forEach((dataSnapshot -> {
                        Route route = new Route();
                        route.setName(String.valueOf(dataSnapshot.getKey()));
                        dataSnapshot.getChildren().forEach((stops -> {
                            route.stops.add(new Route.Stop(stops.getKey()));
                        }));
                        routes.add(route);
                    }));


                    Collections.sort(routes, new Comparator<Route>() {
                        public int compare(Route o1, Route o2) {
                            return o1.name.compareTo(o2.name);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                RouteAdapter adapter = new RouteAdapter(routes);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map_, container, false);
        recyclerView = view.findViewById(R.id.routeRecyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getActivity().getApplicationContext()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }


}