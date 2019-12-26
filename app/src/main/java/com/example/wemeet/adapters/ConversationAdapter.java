package com.example.wemeet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wemeet.R;
import com.example.wemeet.classes.Conversation;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.Message;
import com.example.wemeet.classes.User;
import com.example.wemeet.ui.RegistrationActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private ArrayList<Message> messages;
    private HashMap<String, Bitmap> profileImages;
    private String userID;

    private LayoutInflater inflater;
    private Context context;

    public ConversationAdapter(Context context, ArrayList<Message> messages, String userID) {
        inflater = LayoutInflater.from(context);
        this.messages = messages;
        this.context = context;
        this.userID = userID;
        profileImages = new HashMap<>();
    }


    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_conversation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.MyViewHolder holder, int position) {
        Message selectedMessage = messages.get(position);
        holder.setData(selectedMessage, position);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView, nameTextView;
        de.hdodenhof.circleimageview.CircleImageView senderImageView, receiverImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            senderImageView = itemView.findViewById(R.id.senderImageView);
            receiverImageView = itemView.findViewById(R.id.receiverImageView);

        }

        public void setData(Message message, int position) {

            this.receiverImageView.setVisibility(View.GONE);
            this.senderImageView.setVisibility(View.GONE);

            String senderID = message.getSenderRef().getId();
            this.contentTextView.setText(message.getContent());
            if(profileImages.containsKey(senderID)){
                setImage(senderID, profileImages.get(senderID));
            } else {
                message.getSenderRef().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final User user = documentSnapshot.toObject(User.class);
                        Glide.with(context)
                                .asBitmap()
                                .load(user.getProfilePictureURL())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap image, @Nullable Transition<? super Bitmap> transition) {
                                        setImage(user.getUserID(), image);
                                        profileImages.put(user.getUserID(), image);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });
            }
        }

        private void setImage(String senderID, Bitmap image) {

            if(senderID.equals(userID)){
                senderImageView.setImageBitmap(image);
                senderImageView.setVisibility(View.VISIBLE);
                receiverImageView.setVisibility(View.GONE);
            } else {
                receiverImageView.setImageBitmap(image);
                senderImageView.setVisibility(View.GONE);
                receiverImageView.setVisibility(View.VISIBLE);
            }
        }

    }
}
