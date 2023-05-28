package com.example.fasttransithub.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fasttransithub.R;
import com.example.fasttransithub.Util.Route;
import com.example.fasttransithub.Util.Student;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class UserSignupActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private FirebaseStorage storage;
    boolean imageSelected = false;
    StorageReference storageRef;
    ImageView imageView;
    EditText nameView;
    EditText rollNoView;
    EditText emailView;
    EditText passwordView;
    EditText phoneView;
    Spinner busStopView;
    Button registerButton;

    ArrayList<Route> routes=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance("gs://fast-transit-hub.appspot.com");
        storageRef = storage.getReference().child("images");
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Routes");


        imageView = findViewById(R.id.profileImage);
        nameView = findViewById(R.id.name);
        rollNoView = findViewById(R.id.rollNo);
        emailView = findViewById(R.id.registeremail);
        passwordView = findViewById(R.id.registerpassword);
        phoneView = findViewById(R.id.phoneNo);
        busStopView = findViewById(R.id.busStops);
        registerButton = findViewById(R.id.btnRegister);

        Spinner spinner = (Spinner) findViewById(R.id.busStops);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,new ArrayList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        busStopView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> busStops = new ArrayList<>();
                routes=new ArrayList();
                try {
                    snapshot.getChildren().forEach((dataSnapshot -> {
                        Route route = new Route();
                        route.setName(String.valueOf(dataSnapshot.getKey()));
                        dataSnapshot.getChildren().forEach((stops -> {
                            route.stops.add(new Route.Stop(stops.getKey()));
                        }));
                        routes.add(route);
                    }));



                    for (Route route : routes) {
                        for (Route.Stop stop : route.stops)
                            busStops.add(stop.getName());
                    }

                    Collections.sort(busStops, new Comparator<String>() {
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


                ArrayAdapter<String> adapter = new ArrayAdapter(UserSignupActivity.this,android.R.layout.simple_spinner_item,busStops);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                busStopView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void Select_Image(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 103);
    }

    public void Register(View view) {
        if (!ValidateInputs()) {
            return;
        }
        registerButton.setEnabled(false);

        Upload_Image();


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
                imageSelected=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean ValidateInputs() {
        boolean flag=true;
        String email = emailView.getText().toString();

        if (nameView.getText().toString().isEmpty()) {
            nameView.setError("name not valid");flag=false;
        }
        if(busStopView.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this, "Select a route", Toast.LENGTH_SHORT).show();flag=false;
        }
        if (!imageSelected) {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
            flag=false;
        }
        if (rollNoView.getText().toString().isEmpty()) {
            rollNoView.setError("rollNo not valid");flag=false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailView.setError("email not valid");flag=false;
        }
        if (passwordView.getText().toString().isEmpty()) {
            passwordView.setError("password not valid");flag=false;
        }
        if (phoneView.getText().toString().isEmpty()) {
            phoneView.setError("phone not valid");flag=false;
        }
//        if (busStopView.getText().toString().isEmpty()) {
//            busStopView.setError("busStop not valid");flag=false;
//        }
        return flag;
    }

    private Task<Uri> Upload_Image(){
        firebaseAuth.signInAnonymously();
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String randomFileName = UUID.randomUUID().toString()+"name";
        StorageReference imageRef=storageRef.child(randomFileName);
        UploadTask uploadTask = imageRef.putBytes(data);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.signOut();
                    Uri downloadUri = task.getResult();
                    CreateAccount(downloadUri.toString());
                } else {
                    Toast.makeText(UserSignupActivity.this, "Image upload Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void CreateAccount(String imageUrl){

        String name = nameView.getText().toString();
        String busStop = busStopView.getSelectedItem().toString();
        String email = emailView.getText().toString();
        String phone = phoneView.getText().toString();
        String rollNo = rollNoView.getText().toString();
        String password = passwordView.getText().toString();
        String route = "1";

        boolean routeFound = false;
        for (Route route1 : routes) {
            if (routeFound) break;
            for (Route.Stop stop : route1.stops) {
                if (busStop.equals(stop.getName())) {
                    route = route1.name;
                    routeFound = true;
                }
            }
        }

        Student student = new Student(name, rollNo, busStop, route, phone, email, imageUrl);


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                DatabaseReference studentReference = database.getReference("Student/" + user.getUid());
                                studentReference.setValue(student);
                            }
                            firebaseAuth.signOut();
                            Intent intent = new Intent(UserSignupActivity.this, UserLoginActivity.class);
                            startActivity(intent);
                            registerButton.setEnabled(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            try {
                                Toast.makeText(UserSignupActivity.this, task.getException().getMessage().substring(0, task.getException().getMessage().indexOf('.')),
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            registerButton.setEnabled(true);
                        }
                    }
                });
    }
}