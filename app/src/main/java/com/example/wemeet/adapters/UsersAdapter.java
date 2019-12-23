package com.example.wemeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.classes.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    User thisUser;
    ArrayList<User> usersArrayList;
    LayoutInflater inflater;
    Context context;

    public UsersAdapter(Context context, ArrayList<User> eventArrayList, final User thisUser) {
        inflater = LayoutInflater.from(context);
        this.usersArrayList = eventArrayList;
        this.context = context;
        this.thisUser = thisUser;
    }


    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_user, parent, false);
        UsersAdapter.MyViewHolder holder = new UsersAdapter.MyViewHolder(view);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsersAdapter.MyViewHolder holder, int position) {
        User selectedUser = usersArrayList.get(position);
        holder.setData(selectedUser, position);

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        de.hdodenhof.circleimageview.CircleImageView userImageView;
        ImageView addButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            addButton = itemView.findViewById(R.id.addButton);

        }

        public void setData(final User user, int position) {

            this.nameTextView.setText(user.getDisplayName());
            Glide.with(userImageView)
                    .load(user.getProfilePictureURL())
                    .into(userImageView);

            addButton.setImageResource(R.drawable.ic_add_circle_dark_blue_24dp);
            addButton.setClickable(true);

            if(thisUser.getFriendList() != null) {
                for (DocumentReference ref :
                        thisUser.getFriendList()) {
                    if(ref.getId().equals(user.getUserID())){
                        addButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
                        addButton.setClickable(false);
                        break;
                    }
                }
            }

            if(addButton.isClickable()) {
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(thisUser.getUserID());
                        DocumentReference friendRef = FirebaseFirestore.getInstance().collection("users").document(user.getUserID());
                        userRef.update("friendList", FieldValue.arrayUnion(friendRef));
                        friendRef.update("friendList", FieldValue.arrayUnion(userRef));
                        addButton.setImageResource(R.drawable.ic_check_circle_green_24dp);
                        addButton.setClickable(false);
                    }
                });
            }
        }


        @Override
        public void onClick(View v) {
            //int position = getAdapterPosition();
            //String selectedGuideID = eventArrayList.get(position).getUserID();
            //Intent intent = new Intent(context, GuideDetailsActivity.class);
            //intent.putExtra("guideID", selectedGuideID);
            //intent.putExtra("selectedDate", dateText);
            //context.startActivity(intent);
        }
    }
}
