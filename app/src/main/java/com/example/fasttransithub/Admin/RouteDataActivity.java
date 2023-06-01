package com.example.fasttransithub.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserSignupActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.example.fasttransithub.Util.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class RouteDataActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private TimePicker timePicker;
    private Button showPopupButton;
    private EditText stopName;
    private Dialog popupDialog;
    Route route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_data);
//        timePicker = findViewById(R.id.timePicker1);
        showPopupButton = findViewById(R.id.timeButton);
        stopName = findViewById(R.id.stopName);


        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Routes");

        showPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        route = new Route();
        route.name = intent.getStringExtra("route");
        databaseReference = databaseReference.child(route.name);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                        route.setName(String.valueOf(snapshot.getKey()));
                    snapshot.getChildren().forEach((stops -> {
                            route.stops.add(new Route.Stop(stops.getKey()));
                    }));
                    route.stops.sort(new Comparator<Route.Stop>() {
                        public int compare(Route.Stop o1, Route.Stop o2) {
                            return o1.getTime().compareTo(o2.getTime());
                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                Toast.makeText(RouteDataActivity.this, route.stops.toString(), Toast.LENGTH_SHORT).show();

//                ArrayAdapter<String> adapter = new ArrayAdapter(UserSignupActivity.this,android.R.layout.simple_spinner_item,busStops);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                busStopView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showPopup() {
        // Inflate the popup layout
        popupDialog = new Dialog(RouteDataActivity.this);
        popupDialog.setContentView(R.layout.popup_time_layout);

        // Find the views in the popup layout
        TimePicker popupTimePicker = popupDialog.findViewById(R.id.timePicker1);
        Button okButton = popupDialog.findViewById(R.id.okButton);

        // Set the initial time from the main screen
//        int hour = timePicker.getCurrentHour();
//        int minute = timePicker.getCurrentMinute();
//        popupTimePicker.setCurrentHour(hour);
//        popupTimePicker.setCurrentMinute(minute);

        // Handle OK button click
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected time from the popup TimePicker
                int selectedHour = popupTimePicker.getCurrentHour();
                int selectedMinute = popupTimePicker.getCurrentMinute();

                // Display the selected time on the main screen
                String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                showPopupButton.setText(time);
                Toast.makeText(RouteDataActivity.this, "Selected time: " + time, Toast.LENGTH_SHORT).show();

                // Close the popup
                popupDialog.dismiss();
            }
        });

        // Show the popup dialog
        popupDialog.show();
    }

    public void Add_Stop(View view) {
        Route.Stop stop = new Route.Stop(stopName.getText().toString(),showPopupButton.getText().toString());
        if(stop.getTime().equals("Select Time")){
            Toast.makeText(RouteDataActivity.this, "Select time", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(stop.getName().isEmpty()){
            Toast.makeText(RouteDataActivity.this, "Enter name of the stop", Toast.LENGTH_SHORT).show();
            return;
        }

        route.stops.add(stop);

        databaseReference.child(stop.getName()).setValue(stop.getTime()).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RouteDataActivity.this, "Scheduele Updated", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(RouteDataActivity.this,DashboardActivity.class);
//                startActivity(intent);
            }
        });


    }
}