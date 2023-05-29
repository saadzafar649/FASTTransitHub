package com.example.fasttransithub.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fasttransithub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity
        implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    user_home_Fragment user_home_Fragment = new user_home_Fragment();
    user_schedule_Fragment user_schedule_Fragment = new user_schedule_Fragment();
    user_Account_Fragment user_Account_Fragment = new user_Account_Fragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home, user_home_Fragment)
                        .commit();
                return true;

            case R.id.schedule:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.schedule,user_schedule_Fragment)
                        .commit();
                return true;

            case R.id.account:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.account,  user_Account_Fragment)
                        .commit();
                return true;
        }
        return false;
    }
}
