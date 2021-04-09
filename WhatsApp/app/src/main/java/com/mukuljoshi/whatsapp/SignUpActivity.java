package com.mukuljoshi.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mukuljoshi.whatsapp.Models.Users;
import com.mukuljoshi.whatsapp.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We're creating your account");

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username=(binding.etusername.getEditableText()).toString();
                final String email = (binding.etemail.getEditableText()).toString();
                final String pass = (binding.etpassword.getEditableText()).toString();

                if(username.isEmpty()){
                    binding.etusername.setError("Username is required");
                    binding.etusername.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.etemail.setError("Please provide valid email");
                    binding.etemail.requestFocus();
                    return;
                }
                if (pass.isEmpty()){
                    binding.etpassword.setError("Password is required");
                    binding.etpassword.requestFocus();
                    return;
                }


                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.etemail.getText().toString(),
                        binding.etpassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Users user =
                                            new Users(binding.etusername.getText().toString(),
                                                    binding.etemail.getText().toString(),
                                                    binding.etpassword.getText().toString());
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    Toast.makeText(SignUpActivity.this,"User created " +
                                            "Successfully",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        binding.tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,signin_activity.class);
                startActivity(intent);
            }
        });


    }
}