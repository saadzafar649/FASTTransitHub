package com.example.fasttransithub.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Manage_Account_Fragment extends Fragment {
    View view;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Student");
        getStudents();
    }
    private void getStudents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Student> students = new ArrayList();
                try {
                    snapshot.getChildren().forEach((dataSnapshot -> {
                        String uid=String.valueOf(dataSnapshot.getKey());
                        Student student = dataSnapshot.getValue(Student.class);
                        student.setUid(uid);
                        students.add(student);
                    }));


                    Collections.sort(students, new Comparator<Student>() {
                        public int compare(Student o1, Student o2) {
                            return o1.rollNo.compareTo(o2.rollNo);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                StudentAdapter adapter = new StudentAdapter(students);
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
        view = inflater.inflate(R.layout.fragment_manage__account_, container, false);
        recyclerView = view.findViewById(R.id.studentsRecyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getActivity().getApplicationContext()));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }
}