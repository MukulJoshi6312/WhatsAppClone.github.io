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
import com.mukuljoshi.whatsapp.databinding.ActivityChatDetailAcivityBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

        ActivityChatDetailAcivityBinding binding;

        FirebaseDatabase database;
        FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.username.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.user_pic).into(binding.profileImage);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels,this,recieveId);
        binding.chatRecylerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecylerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId + recieveId;
        final String receiverRoom = recieveId + senderId;



        database.getReference().child("chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageModels.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren())
                {
                    MessageModel model = dataSnapshot1.getValue(MessageModel.class);

                    model.setMessageId(dataSnapshot1.getKey());


                    messageModels.add(model);
                }
                chatAdapter.notifyDataSetChanged();

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
           final  MessageModel model = new MessageModel(senderId,message);
           model.setTimestamp(new Date().getTime());
           binding.etMessage.setText("");
           database.getReference().child("chats").
                   child(senderRoom)
                   .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   database.getReference().child("chats").child(receiverRoom).
                           push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {

                       }
                   });
               }
           });
            }
        });

    }
}