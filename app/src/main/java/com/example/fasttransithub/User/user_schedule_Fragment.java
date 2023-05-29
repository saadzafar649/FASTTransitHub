package com.example.fasttransithub.User;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fasttransithub.R;
import android.widget.ListView;
import java.util.ArrayList;
public class user_schedule_Fragment extends Fragment {
    private String[] Products = new String[]{"Pizza", "Burger", "Pasta", "Salad", "Rice","Sandwich","Chips"};
    private int[] Qty = new int[]{3, 4, 2, 1, 5,8,20};
    private ArrayList<User_schedule_frag > ArrayList;
    private ListView listView;

    public user_schedule_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_schedule_, container, false);
        listView = view.findViewById(R.id.user_route);

        //foodModelArrayList = new ArrayList<>();
        ArrayList = populateList();

        schedule_adapter foodAdapter = new schedule_adapter(getActivity(),ArrayList);
        listView.setAdapter(foodAdapter);
        // Inflate the layout for this fragment
       return view;


    }
    private ArrayList<User_schedule_frag > populateList(){

        ArrayList<User_schedule_frag > list = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            User_schedule_frag  foodModel = new User_schedule_frag ();
            foodModel.setstop(Products[i]);
            foodModel.settime(Qty[i]);
            list.add(foodModel);
        }

        return list;
    }
}