package com.example.fasttransithub.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fasttransithub.R;
import com.example.fasttransithub.User.BgService;
import com.example.fasttransithub.User.DashboardActivity;
import com.example.fasttransithub.Util.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences ;
    EditText emailfield;
    EditText passwordfield;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailfield = findViewById(R.id.loginemail) ;
        passwordfield= findViewById(R.id.loginPassword);


        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://fast-transit-hub-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("Admins");
        sharedPreferences = getSharedPreferences("myFile",0);
    }

    public void Login(View view) {
        Boolean error = false;
        String email,password;
        email = emailfield.getText().toString();
        password = passwordfield.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailfield.setError("Enter valid email");
            error=true;
        }
        if(password.isEmpty()){
            passwordfield.setError("Enter password");
            error=true;
        }
        if(error)return;

        view.setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (task.isSuccessful()) {
                            Toast.makeText(UserLoginActivity.this, "asd",
                                    Toast.LENGTH_LONG).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    AtomicBoolean isAdmin = new AtomicBoolean(false);
                                    snapshot.getChildren().forEach(dataSnapshot -> {
                                        if (dataSnapshot.getValue().equals(user.getUid())) {
                                            isAdmin.set(true);
                                        }
                                    });
                                    if (isAdmin.get()) {
                                        editor.putString("userType", "Admin");
                                        editor.commit();

                                        Intent intent= new Intent(UserLoginActivity.this, com.example.fasttransithub.Admin.DashboardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else {
                                        editor.putString("userType", "Student");
                                        editor.commit();

                                        Intent serviceIntent = new Intent(UserLoginActivity.this, BgService.class);
                                        startService(serviceIntent);

                                        Intent intent= new Intent(UserLoginActivity.this, DashboardActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }

                                    view.setEnabled(true);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(UserLoginActivity.this, task.getException().getMessage().substring(0,task.getException().getMessage().indexOf('.')),
                                    Toast.LENGTH_LONG).show();
                            view.setEnabled(true);
                        }
                    }
                });
    }

    public void Register_User(View view) {

        Intent intent= new Intent(UserLoginActivity.this, UserSignupActivity.class);
        startActivity(intent);
    }
}