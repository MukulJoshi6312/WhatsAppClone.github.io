package com.mukuljoshi.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukuljoshi.whatsapp.Models.Users;
import com.mukuljoshi.whatsapp.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static java.util.Objects.requireNonNull;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this,MainActivity.class);
                startActivity(intent);
            }
        });


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = binding.etStatus.getText().toString();
                String username = binding.etUserName.getText().toString();

                HashMap<String , Object> obj= new HashMap<>();
                obj.put("userName",username);
                obj.put("status",status);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(Settings.this, "Profile Updated", Toast.LENGTH_SHORT).show();

            }
        });




        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Users users = snapshot.getValue(Users.class);
                        Picasso.get()
                                .load(users.getPorfilepic())
                                .placeholder(R.drawable.user_pic)
                                .into(binding.profileImage);

                        binding.etStatus.setText(users.getStatus());
                        binding.etUserName.setText(users.getUserName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if(data.getData() != null){
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);
            final StorageReference reference = storage.getReference().child("profile_picures")
                    .child(requireNonNull(FirebaseAuth.getInstance().getUid()));
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                .child("porfilepic").setValue(uri.toString());
                            Toast.makeText(Settings.this, "Profile picture updated",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
    }
}