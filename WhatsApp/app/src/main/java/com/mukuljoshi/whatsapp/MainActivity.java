package com.mukuljoshi.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mukuljoshi.whatsapp.Adapters.FragmentsAdapter;
import com.mukuljoshi.whatsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

//        progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setTitle("Logout");
//        progressDialog.setMessage("Logout your account!");

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.setting:
                Intent intent2 = new Intent(MainActivity.this,Settings.class);
                startActivity(intent2);
                break;
            case R.id.logout:
               // progressDialog.show();
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,signin_activity.class);
               // progressDialog.show();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
                break;

            case R.id.groupChat:
                Intent intent1 = new Intent(MainActivity.this,GrouChat.class);
                startActivity(intent1);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return true;

        //return super.onOptionsItemSelected(item);
    }

}