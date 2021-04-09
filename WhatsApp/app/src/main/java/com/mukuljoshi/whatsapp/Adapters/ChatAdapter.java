package com.mukuljoshi.whatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mukuljoshi.whatsapp.Models.MessageModel;
import com.mukuljoshi.whatsapp.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messageModels;
    Context context;
    String resId;

    int SENDER_TYPE = 1;
    int RECEIVER_TYPE = 2;

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String resId) {
        this.messageModels = messageModels;
        this.context = context;
        this.resId = resId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else {

            View view = LayoutInflater.from(context).inflate(R.layout.sample_receverr,parent,false);
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_TYPE;
        }else {
           return RECEIVER_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MessageModel messageModel = messageModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String sender = FirebaseAuth.getInstance().getUid() + resId;
                                database.getReference().child("chats").child(sender)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return false;
            }
        });


        if (holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messageModel.getMessage());

        }
        else {
            ((ReciverViewHolder)holder).reciverMsg.setText(messageModel.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }







    public class ReciverViewHolder extends RecyclerView.ViewHolder{

       TextView reciverMsg, reciverTime;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverMsg = itemView.findViewById(R.id.reciever_text);
            reciverTime = itemView.findViewById(R.id.reciever_time);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.sender_text);
            senderTime = itemView.findViewById(R.id.sender_time);

        }
    }

}
