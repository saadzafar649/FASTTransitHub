package com.example.fasttransithub.User;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fasttransithub.Admin.StudentDataActivity;
import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class user_home_Fragment extends Fragment {

    ImageView qrCodeIV;
    ImageView imageView;
    TextView rollNoView;
    TextView nameView;
    TextView busStopView;
    TextView busStopTimeView;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Bitmap bitmap;
    String uid;
    Student student;

    public user_home_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_home_, container, false);
        qrCodeIV = view.findViewById(R.id.user_Qr);
        rollNoView = view.findViewById(R.id.user_rollno);
        nameView = view.findViewById(R.id.user_Name);
        busStopView = view.findViewById(R.id.user_Route);
        busStopTimeView = view.findViewById(R.id.user_rout_time);
        imageView = view.findViewById(R.id.user_profileImage);

        if (firebaseAuth.getCurrentUser() == null)
            return view;

        uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = database.getReference("Student").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    student = snapshot.getValue(Student.class);
                    student.verified = String.valueOf(((HashMap<String, Object>) snapshot.getValue()).get("verified")) == "true";
                    rollNoView.setText(student.getRollNo());
                    nameView.setText(student.getName());
                    busStopView.setText(student.getBusStop());

                    databaseReference = database.getReference("Routes").child(student.getRoute()).child(student.getBusStop());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            busStopTimeView.setText(snapshot2.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                initQRCode();
                Download_Image_AsyncTask(student.imageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void initQRCode() {
        String name = student.toString();

        StringBuilder textToSend = new StringBuilder();
        textToSend.append(name);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(textToSend.toString(), BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeIV.setImageBitmap(bitmap);
            qrCodeIV.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
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




    public static class DownloadImage extends AsyncTask<String, Void, Bitmap> {

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