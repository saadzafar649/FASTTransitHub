package com.example.fasttransithub.User;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.example.fasttransithub.Util.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class user_schedule_Fragment extends Fragment {
    private ArrayList<String> StopNames = new ArrayList<>();
    private ArrayList<String> StopTimes = new ArrayList<>();
    private ArrayList<User_schedule_frag > ArrayList;
    private ListView listView;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Student student;

    public user_schedule_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_schedule_, container, false);
        listView = view.findViewById(R.id.user_route);

        //foodModelArrayList = new ArrayList<>();


        if (firebaseAuth.getCurrentUser() == null)
            return view;

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
                            StopNames=new ArrayList<>();
                            StopTimes=new ArrayList<>();
                            snapshot2.getChildren().forEach((dataSnapshot -> {
                                StopNames.add(dataSnapshot.getKey().toString());
                                StopTimes.add(dataSnapshot.getValue().toString());
                            }));


                            ArrayList<User_schedule_frag > array = populateList();

                            schedule_adapter foodAdapter = new schedule_adapter(getActivity(),array);
                            listView.setAdapter(foodAdapter);
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
        
        
        // Inflate the layout for this fragment
       return view;


    }
    private ArrayList<User_schedule_frag > populateList(){

        ArrayList<User_schedule_frag > list = new ArrayList<>();

        for(int i = 0; i < StopNames.size(); i++){
            User_schedule_frag  scheduele = new User_schedule_frag ();
            scheduele.setstop(StopNames.get(i));
            scheduele.settime(StopTimes.get(i));
            list.add(scheduele);
        }

        return list;
    }
}