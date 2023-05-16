package com.example.fasttransithub.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fasttransithub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText emailfield;
    EditText passwordfield;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        emailfield = findViewById(R.id.loginemail) ;
        passwordfield= findViewById(R.id.loginpassword);

        firebaseAuth = FirebaseAuth.getInstance();
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
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(UserLoginActivity.this, user.getUid(),
                                    Toast.LENGTH_SHORT).show();
                            view.setEnabled(true);
                            firebaseAuth.signOut();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("TAG", "signInWithEmail:failure"+String.valueOf(task.hashCode()) , task.getException());
                            Toast.makeText(UserLoginActivity.this, task.getException().getMessage().substring(0,task.getException().getMessage().indexOf('.')),
                                    Toast.LENGTH_LONG).show();
                            view.setEnabled(true);
                            firebaseAuth.signOut();
//                            updateUI(null);
                        }
                    }
                });
    }
}