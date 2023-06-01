package com.example.fasttransithub.Admin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserSignupActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private static final String CHANNEL_ID = "my_channel_id";

    View view;
    EditText routeName;
    Button addRoute;

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
        routeName = view.findViewById(R.id.routeName);
        addRoute = view.findViewById(R.id.addRoute);
        addRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Route(v);
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, 1);
//                builder.setContentTitle("Picture Download")
//                        .setContentText("Download in progress")
//                        .setSmallIcon(R.drawable.busstop_icon)
//                        .setPriority(NotificationCompat.PRIORITY_LOW);
//                Toast.makeText(getContext(), "Notification", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getActivity().getApplicationContext()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }


    public void Add_Route(View view) {
        String name = routeName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Enter name of the Route", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(name).setValue("null").addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Route added", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(RouteDataActivity.this,DashboardActivity.class);
//                startActivity(intent);
            }
        });

    }
}