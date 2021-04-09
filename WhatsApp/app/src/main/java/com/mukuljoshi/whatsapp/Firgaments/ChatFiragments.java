package com.mukuljoshi.whatsapp.Firgaments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukuljoshi.whatsapp.Adapters.UsersAdapter;
import com.mukuljoshi.whatsapp.Models.Users;
import com.mukuljoshi.whatsapp.databinding.FragmentChatFiragmentsBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFiragments} factory method to
 * create an instance of this fragment.
 */
public class ChatFiragments extends Fragment {

    public ChatFiragments() {
        // Required empty public constructor
    }

    FragmentChatFiragmentsBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatFiragmentsBinding.inflate(inflater, container, false);


        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait");



        final UsersAdapter adapter = new UsersAdapter(list,getContext());
        binding.chatRecylerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatRecylerView.setLayoutManager(linearLayoutManager);
        progressDialog.show();

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserID(dataSnapshot.getKey());
                    if (!users.getUserID().equals(FirebaseAuth.getInstance().getUid())){
                    list.add(users);}
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return binding.getRoot();
    }
}