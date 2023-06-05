package com.example.fasttransithub.Admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserLoginActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Home_Fragment extends Fragment {
    PieChart pieChart;
    TextView paidView,unpaidView;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    int countpaid=0,countunpaid=0;
    Button logoutButton;
    public Home_Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Student");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_, container, false);
        pieChart = view.findViewById(R.id.piechart);
        paidView = view.findViewById(R.id.countpaid);
        unpaidView = view.findViewById(R.id.countunpaid);
        logoutButton=view.findViewById(R.id.logout_btn);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(v);
            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countpaid=0;countunpaid=0;
                ArrayList<Student> students = new ArrayList();
                try {
                    snapshot.getChildren().forEach((dataSnapshot -> {
                        String uid=String.valueOf(dataSnapshot.getKey());
                        try {
                            Student student = dataSnapshot.getValue(Student.class);
//                            student.verified = String.valueOf(((HashMap<String, Object>)snapshot.getValue()).get("verified"))=="true";
                            if(student.verified)countpaid++;
                            else countunpaid++;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));


                    Collections.sort(students, new Comparator<Student>() {
                        public int compare(Student o1, Student o2) {
                            return o1.rollNo.compareTo(o2.rollNo);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                pieChart.clearChart();

                pieChart.addPieSlice(
                        new PieModel(
                                "Paid Students",
                                (countpaid),
                                Color.parseColor("#66BB6A")));
                pieChart.addPieSlice(
                        new PieModel(
                                "Unpaid Students",
                                (countunpaid),
                                Color.parseColor("#EF5350")));

                pieChart.startAnimation();

                paidView.setText(String.valueOf(countpaid));
                unpaidView.setText(String.valueOf(countunpaid));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










        return view;
    }

    public void Logout(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), UserLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}