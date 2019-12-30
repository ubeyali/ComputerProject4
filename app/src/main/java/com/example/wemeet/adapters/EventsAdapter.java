package com.example.wemeet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.classes.Conversation;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.User;
import com.example.wemeet.ui.ConversationActivity;
import com.example.wemeet.ui.EventDetailsActivity;
import com.example.wemeet.ui.EventOwnerActivity;
import com.example.wemeet.ui.RegistrationActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    private ArrayList<Event> eventArrayList;
    private String userID;
    private LayoutInflater inflater;
    private Context context;

    public EventsAdapter(Context context, ArrayList<Event> eventArrayList, String userID) {
        inflater = LayoutInflater.from(context);
        this.eventArrayList = eventArrayList;
        this.context = context;
        this.userID = userID;
    }


    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_event, parent, false);
        EventsAdapter.MyViewHolder holder = new EventsAdapter.MyViewHolder(view);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, int position) {
        Event selectedEvent = eventArrayList.get(position);
        holder.setData(selectedEvent, position);

    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView eventNameTextView, ownerNameTextView, lastActivityTextView, peopleTextView, chatTextView;
        de.hdodenhof.circleimageview.CircleImageView ownerProfileImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            lastActivityTextView = itemView.findViewById(R.id.lastActivityTextView);
            peopleTextView = itemView.findViewById(R.id.peopleTextView);
            chatTextView = itemView.findViewById(R.id.chatTextView);
            ownerProfileImageView = itemView.findViewById(R.id.userImageView);

        }

        public void setData(final Event event, int position) {

            this.eventNameTextView.setText(event.getTitle());
            this.lastActivityTextView.setText("");
            HashMap<String, Boolean> peopleCount = new HashMap<>();
            for(Date date : event.getDateTimeList()){
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                final String strDate = dateFormat.format(date);
                if(event.getRegistrations() != null && event.getRegistrations().containsKey(strDate)){
                    for(DocumentReference reference : event.getRegistrations().get(strDate)){
                        peopleCount.put(reference.getId(), true);
                    }
                }
            }
            String peopleText;
            if(peopleCount.size() > 1){
                peopleText = String.format("You and %d others", peopleCount.size() - 1);
            } else if(peopleCount.size() == 1) {
                peopleText = "You";
            } else {
                peopleText = "No registrations";
            }
            this.peopleTextView.setText(peopleText);
            if(event.getConversationReference() == null) {
                chatTextView.setText(String.valueOf(0));
            } else {
                event.getConversationReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Conversation conversation = documentSnapshot.toObject(Conversation.class);
                        if(conversation.getMessages() == null) {
                            chatTextView.setText(String.valueOf(0));
                        } else {
                            chatTextView.setText(String.valueOf(conversation.getMessages().size()));
                        }
                    }
                });
            }


            this.chatTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String conversationID;
                    if(event.getConversationReference() != null){
                        conversationID = event.getConversationReference().getId();
                        openConversation(conversationID);
                    } else {
                        conversationID = UUID.randomUUID().toString();
                        final FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
                        final DocumentReference conversationReference = mDatabase.collection("conversations").document(conversationID);
                        Conversation conversation = new Conversation();
                        conversation.setConversationID(conversationID);
                        conversationReference.set(conversation).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabase.collection("events").document(event.getEventID())
                                        .update("conversationReference", conversationReference).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        openConversation(conversationID);
                                    }
                                });
                            }
                        });
                    }
                }
            });

            event.getOwner().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User owner = documentSnapshot.toObject(User.class);
                    ownerNameTextView.setText(owner.getDisplayName());
                    Glide.with(ownerProfileImageView)
                            .load(owner.getProfilePictureURL())
                            .into(ownerProfileImageView);
                }
            });
        }

        private void openConversation(String conversationID) {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra("conversationID", conversationID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Event selectedEvent = eventArrayList.get(position);
            String selectedEventID = selectedEvent.getEventID();
            Intent intent;
            if(selectedEvent.getOwner().getId().equals(userID)){
                intent = new Intent(context, EventOwnerActivity.class);
            } else {
                intent = new Intent(context, EventDetailsActivity.class);
            }
            intent.putExtra("eventID", selectedEventID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent);
        }
    }
}
