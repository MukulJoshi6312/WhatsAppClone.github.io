package com.mukuljoshi.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukuljoshi.whatsapp.Adapters.ChatAdapter;
import com.mukuljoshi.whatsapp.Models.MessageModel;
import com.mukuljoshi.whatsapp.databinding.ActivityGrouChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class GrouChat extends AppCompatActivity {

    ActivityGrouChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrouChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GrouChat.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();

        binding.username.setText("Friends Group");

        final ChatAdapter adapter = new ChatAdapter(messageModels, this);
        binding.chatRecylerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecylerView.setLayoutManager(layoutManager);

        database.getReference().child("Group Chat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            MessageModel model = dataSnapshot.getValue(MessageModel.class);
                            messageModels.add(model);

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = binding.etMessage.getText().toString();
                if (message.isEmpty()){
                    binding.etMessage.setError("Message is required");
                    binding.etMessage.requestFocus();
                    return;

                }
                final MessageModel model = new MessageModel(senderId,message);
                model.setTimestamp(new Date().getTime());

                binding.etMessage.setText("");

                database.getReference().child("Group Chat").push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });



    }
}