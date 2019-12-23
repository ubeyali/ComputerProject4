package com.example.wemeet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.classes.Event;
import com.example.wemeet.classes.User;
import com.example.wemeet.ui.RegisterActivity;
import com.example.wemeet.ui.RegistrationActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    ArrayList<Event> eventArrayList;
    LayoutInflater inflater;
    Context context;

    public EventsAdapter(Context context, ArrayList<Event> eventArrayList) {
        inflater = LayoutInflater.from(context);
        this.eventArrayList = eventArrayList;
        this.context = context;
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

        public void setData(Event event, int position) {

            this.eventNameTextView.setText(event.getTitle());
            this.lastActivityTextView.setText("27 min");
            String peopleText = String.format("You and %d others", event.getInvitedCount() - 1);
            this.peopleTextView.setText(peopleText);
            this.chatTextView.setText(String.valueOf(event.getMessageCount()));

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


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String selectedEventID = eventArrayList.get(position).getEventID();
            Intent intent = new Intent(context, RegistrationActivity.class);
            intent.putExtra("eventID", selectedEventID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            context.startActivity(intent);
        }
    }
}