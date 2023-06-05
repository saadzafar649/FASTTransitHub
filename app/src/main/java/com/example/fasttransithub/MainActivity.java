package com.example.fasttransithub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserLoginActivity;
import com.example.fasttransithub.User.BgService;
import com.example.fasttransithub.User.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "my_channel_id";

    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();


        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("myFile", 0);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && sharedPreferences.getString("userType", null) != null) {
            if (sharedPreferences.getString("userType", null).equals("Admin")) {
                Intent intent = new Intent(this, com.example.fasttransithub.Admin.DashboardActivity.class);
                startActivity(intent);
            } else if (sharedPreferences.getString("userType", null).equals("Student")) {
                Intent serviceIntent = new Intent(this, BgService.class);
                startService(serviceIntent);


                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(this, UserLoginActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "discription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.createNotificationChannel(channel);
        }
    }
}
