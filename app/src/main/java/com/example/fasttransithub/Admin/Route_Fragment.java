package com.example.fasttransithub.Admin;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
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
        createNotificationChannel();

        view = inflater.inflate(R.layout.fragment_map_, container, false);
        recyclerView = view.findViewById(R.id.routeRecyclerView);
        routeName = view.findViewById(R.id.routeName);
        addRoute = view.findViewById(R.id.addRoute);
        addRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Route(v);
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.fasttransithub");
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, "channel");
                startActivity(intent);
                Intent intent1 = new Intent(getContext(),DashboardActivity.class);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
                builder.setContentTitle("Route added")
                        .setContentText(routeName.getText().toString())
                        .setSmallIcon(R.drawable.email_icon)
                        .setPriority(NotificationCompat.PRIORITY_MAX);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManager.notify(1, builder.build());

                Toast.makeText(getContext(), "Notification", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getActivity().getApplicationContext()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "discription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.createNotificationChannel(channel);
        }
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