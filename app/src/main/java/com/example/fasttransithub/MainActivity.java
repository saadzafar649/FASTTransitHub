package com.example.fasttransithub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserLoginActivity;
import com.example.fasttransithub.User.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("myFile", 0);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null && sharedPreferences.getString("userType", null) != null) {
            if (sharedPreferences.getString("userType", null).equals("Admin")) {
                Intent intent = new Intent(this, com.example.fasttransithub.Admin.DashboardActivity.class);
                startActivity(intent);
            } else if (sharedPreferences.getString("userType", null).equals("Student")) {
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
}
