package com.mukuljoshi.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.mukuljoshi.whatsapp.Models.Users;
import com.mukuljoshi.whatsapp.databinding.ActivitySigninActivityBinding;

public class signin_activity extends AppCompatActivity {

    ActivitySigninActivityBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database =FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(signin_activity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
                googleSignInClient = GoogleSignIn.getClient(this,gso);


        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = (binding.etemail.getEditableText()).toString();
                final String pass = (binding.etpassword.getEditableText()).toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(name).matches()){
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
                auth.signInWithEmailAndPassword(binding.etemail.getText().toString(),
                        binding.etpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(signin_activity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(signin_activity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });

        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signin_activity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        //sign in with google

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        // sign in with facebook

        binding.btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });




        if (auth.getCurrentUser()!= null){
            Intent intent = new Intent(signin_activity.this,MainActivity.class);
            startActivity(intent);

        }
    }

    int RC_SIGN_IN = 70;
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setTitle("Google");
        progressDialog.setMessage("Sign With Google");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Users users = new Users();
                            users.setUserID(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setPorfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            Intent intent = new Intent(signin_activity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(signin_activity.this, "Sign In with Google",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                           //  updateUI(null);
                        }
                    }
                });
    }





}