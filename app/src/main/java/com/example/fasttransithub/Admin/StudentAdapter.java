package com.example.fasttransithub.Admin;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.example.fasttransithub.Util.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder>{
    private ArrayList<Student> studentList;


    public StudentAdapter(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.M_Name.setText(student.getRollNo());
        holder.M_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StudentDataActivity.class);
                intent.putExtra("uid", student.uid);
                v.getContext().startActivity(intent);
            }
        });
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG",student.getRollNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView M_Name;
        public View delete_button;
        public ViewHolder(@NonNull View v) {
            super(v);
            M_Name = v.findViewById(R.id.routeName);
            delete_button = v.findViewById(R.id.deleteButton);
        }
    }
}
