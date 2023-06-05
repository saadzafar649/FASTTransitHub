package com.example.fasttransithub.User;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fasttransithub.Util.CustomNotification;
import com.example.fasttransithub.Util.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BgService extends Service {
    long timedifference=5;
    Student student;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String stoptime;//"format = 08:55"
    private HandlerThread handlerThread;
    private Handler handler;

    private Runnable stopTimeCheckingTask = new Runnable() {
        @Override
        public void run() {

            String uid = firebaseAuth.getCurrentUser().getUid();
            databaseReference = database.getReference("Student").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        student = snapshot.getValue(Student.class);
                        student.verified = String.valueOf(((HashMap<String, Object>) snapshot.getValue()).get("verified")) == "true";

                        databaseReference = database.getReference("Routes").child(student.getRoute());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                                snapshot2.getChildren().forEach((dataSnapshot -> {

                                    if (student.busStop.equals(dataSnapshot.getKey().toString())) {
                                        String stopTime = dataSnapshot.getValue().toString();

                                        // Get the current time
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                        String currentTime = sdf.format(new Date());

                                        // Parse the stop time and current time
                                        Date stopTimeDate = null;
                                        Date currentTimeDate = null;
                                        try {
                                            stopTimeDate = sdf.parse(stopTime);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        try {
                                            currentTimeDate = sdf.parse(currentTime);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }

                                        // Calculate the time difference in minutes
                                        long timeDifferenceMillis = stopTimeDate.getTime() - currentTimeDate.getTime();
                                        long timeDifferenceMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis);

                                        // Check if the stop time is within the next 5 minutes
                                        if (timedifference!=timeDifferenceMinutes &&
                                                timeDifferenceMinutes <= 5 && timeDifferenceMinutes > 0) {
                                            CustomNotification.showNotification(getApplicationContext(), "my_channel_id", "Your bus will arrive in "+
                                                    String.valueOf(timeDifferenceMinutes)+" minutes", "Be Ready");
                                            timedifference = timeDifferenceMinutes;
                                        }
                                        else{
                                            timedifference = timeDifferenceMinutes;
                                        }
                                    }
                                }));

                                //check if stoptime == currentTime+5minutes
                                //call this == CustomNotification.showNotification(getApplicationContext(),"1","Route Added","yes");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




            // Schedule the next execution after 1 minute
            handler.postDelayed(this, 1 * 1000);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null)
            stopSelf();

        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = database.getReference("Student").child(uid);

        handlerThread = new HandlerThread("StopTimeHandlerThread");
        handlerThread.start();
        // Initialize the handler with the handler thread's looper
        handler = new Handler(handlerThread.getLooper());

        // Start the stop time checking task
        handler.postDelayed(stopTimeCheckingTask, 0);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(stopTimeCheckingTask);

        // Quit the handler thread
        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
}