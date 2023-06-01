package com.example.fasttransithub.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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
    private TableLayout tableLayout;
    View backButton;
    TextView appBar;
    Route route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_data);
//        timePicker = findViewById(R.id.timePicker1);
        showPopupButton = findViewById(R.id.timeButton);
        stopName = findViewById(R.id.stopName);
        tableLayout = findViewById(R.id.tableLayout);
        appBar = findViewById(R.id.appBar);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Routes");

        showPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        // Create the table dynamically
    }
    private void createTable() {
        tableLayout.removeAllViews();
        // Create headers



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    route.stops=new ArrayList<>();
                    route.setName(String.valueOf(snapshot.getKey()));
                    snapshot.getChildren().forEach((stops -> {
                        route.stops.add(new Route.Stop(stops.getKey(),stops.getValue(String.class)));
                    }));
                    route.stops.sort(new Comparator<Route.Stop>() {
                        public int compare(Route.Stop o1, Route.Stop o2) {
                            return o1.getTime().compareTo(o2.getTime());
                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                TableRow headerRow = new TableRow(RouteDataActivity.this);
                headerRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));

                tableLayout.removeAllViews();

                TextView header1 = createTextView("Stop");
                TextView header2 = createTextView("Time");
                TextView header3 = createTextView("Delete");

                headerRow.addView(header1);
                headerRow.addView(header2);
                headerRow.addView(header3);

                tableLayout.addView(headerRow);

                for (Route.Stop stop: route.stops) {
                    TableRow row = new TableRow(RouteDataActivity.this);
                    row.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    ));

                    TextView cell1 = createTextView(stop.getName());
                    TextView cell2 = createTextView(stop.getTime());
                    View cell3 = createDeleteButton(stop.getName());

                    row.addView(cell1);
                    row.addView(cell2);
                    row.addView(cell3);

                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL); // Center align text vertically and horizontally
        textView.setPadding(16, 16, 16, 16);

        return textView;
    }

    private ImageButton createDeleteButton(String text) {
        ImageButton imageButton = new ImageButton(this);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );
        imageButton.setLayoutParams(layoutParams);
        imageButton.setPadding(16, 16, 16, 16);
        imageButton.setImageResource(R.drawable.delete_icon);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(text).removeValue();
            }
        });
        imageButton.setBackgroundResource(R.color.ic_launcher_background);
        return imageButton;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        route = new Route();
        route.name = intent.getStringExtra("route");
        appBar.setText(route.name);
        databaseReference = databaseReference.child(route.name);

        createTable();
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