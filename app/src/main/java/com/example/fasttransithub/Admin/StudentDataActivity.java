package com.example.fasttransithub.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fasttransithub.Authentication.UserSignupActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.example.fasttransithub.Util.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class StudentDataActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ImageView imageView;
    EditText rollNoView, nameView, phoneView;
    TextView routeView, busStopView, emailView;
    SwitchCompat feePaidView;
    Student student;String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);
        rollNoView = findViewById(R.id.stuRollNo);
        nameView = findViewById(R.id.stuName);
        phoneView = findViewById(R.id.stuPhone);
        emailView = findViewById(R.id.stuMail);
        routeView = findViewById(R.id.stuRoute);
        busStopView = findViewById(R.id.stuBusStop);
        feePaidView = findViewById(R.id.feePaid);
        imageView = findViewById(R.id.studentImage);

        String uid = "3rNr2P7GnvaTpflW5rZqsbJGx3O2";

        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");





    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
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
                    emailView.setText(student.getEmail());
                    routeView.setText(student.getRoute());
                    busStopView.setText(student.getBusStop());
                    feePaidView.setChecked(student.verified);
                    Toast.makeText(StudentDataActivity.this, String.valueOf(student.verified) , Toast.LENGTH_SHORT).show();
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

                runOnUiThread(() -> {
                    // Update UI on the main thread
                    imageView.setImageBitmap(bitmap);
                    Log.d("TAG", "Back in Main");
                });
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        newThread.start();
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

    public void Update(View view) {
        student.rollNo = rollNoView.getText().toString();
        student.name = nameView.getText().toString();
        student.phone = phoneView.getText().toString();
        student.verified=(feePaidView.isChecked());
        databaseReference.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(StudentDataActivity.this, "Student Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StudentDataActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}