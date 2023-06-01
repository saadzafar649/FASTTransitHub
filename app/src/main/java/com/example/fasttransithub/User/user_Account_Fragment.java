package com.example.fasttransithub.User;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fasttransithub.Admin.StudentDataActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Student;
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
    TextView  rollNoView, nameView, phoneView,emailView;
    Student student;String uid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String uid = "3rNr2P7GnvaTpflW5rZqsbJGx3O2";
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Student");

        getStudents_account();
    }
    private void getStudents_account() {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        user.getUid();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user__account_, container, false);

        return view;
    }
}