package com.example.fasttransithub.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fasttransithub.Admin.StudentDataActivity;
import com.example.fasttransithub.Authentication.UserLoginActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class user_Account_Fragment extends Fragment {
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ImageView imageView;
    EditText  rollNoView, nameView, phoneView;
    Button updateButton,logoutButton;
    Student student;String uid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = "3rNr2P7GnvaTpflW5rZqsbJGx3O2";
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user__account_, container, false);
        rollNoView =view.findViewById(R.id.acc_rollNo);
        nameView=view.findViewById(R.id.acc_name);
        phoneView=view.findViewById(R.id.acc_phoneNo);
        phoneView=view.findViewById(R.id.acc_phoneNo);
        imageView=view.findViewById(R.id.profile_Image);
        updateButton=view.findViewById(R.id.acc_btn);
        logoutButton=view.findViewById(R.id.logout_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update(v);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(v);
            }
        });
        getStudents_account();

        return view;
    }
    private void getStudents_account() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid =user.getUid();
        databaseReference = database.getReference("Student").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    student = snapshot.getValue(Student.class);
                    student.verified = String.valueOf(((HashMap<String, Object>)snapshot.getValue()).get("verified"))=="true";
                    rollNoView.setText(student.getRollNo());
                    nameView.setText(student.getName());
                    phoneView.setText(student.getPhone());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Download_Image_AsyncTask(student.imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Download_Image_AsyncTask(String url) {
        Thread newThread = new Thread(() -> {
            DownloadImage downloadImage = new DownloadImage();
            try {
                Log.d("TAG", "Button Clicked");
                Bitmap bitmap = downloadImage.execute(url).get();

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        // Update UI on the main thread
                        imageView.setImageBitmap(bitmap);
                        Log.d("TAG", "Back in Main");
                    });
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        newThread.start();
    }

    public void Logout(View view) {
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), UserLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Update(View view) {
        student.rollNo = rollNoView.getText().toString();
        student.name = nameView.getText().toString();
        student.phone = phoneView.getText().toString();
        databaseReference.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Data Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
    }


    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                Log.d("TAG", "doInBackground() in started");
                URL url = new URL(strings[0]);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Log.d("TAG", "doInBackground() in progress");
                return bitmap;

            } catch (MalformedURLException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}