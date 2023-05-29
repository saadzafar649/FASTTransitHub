package com.example.fasttransithub.Admin;

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
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    Route_Fragment route_Fragment = new Route_Fragment();
    Home_Fragment home_fragment = new Home_Fragment();
    Manage_Account_Fragment manage_account_fragment = new Manage_Account_Fragment();

    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId()) {
            case R.id.map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin, route_Fragment)
                        .commit();
                return true;

            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin, home_fragment)
                        .commit();
                return true;

            case R.id.manage_account:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin, manage_account_fragment)
                        .commit();
                return true;
        }
        return false;
    }
}
