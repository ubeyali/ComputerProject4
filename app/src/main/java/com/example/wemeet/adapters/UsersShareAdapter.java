package com.example.wemeet.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.classes.User;
import com.example.wemeet.ui.ShareActivity;
import com.github.tntkhang.gmailsenderlibrary.GMailSender;
import com.github.tntkhang.gmailsenderlibrary.GmailListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersShareAdapter extends RecyclerView.Adapter<UsersShareAdapter.MyViewHolder> {

    User thisUser;
    ArrayList<User> usersArrayList;
    LayoutInflater inflater;
    Context context;


    public UsersShareAdapter(Context context, ArrayList<User> eventArrayList, final User thisUser) {
        inflater = LayoutInflater.from(context);
        this.usersArrayList = eventArrayList;
        this.context = context;
        this.thisUser = thisUser;
    }


    @Override
    public UsersShareAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_user_share, parent, false);
        UsersShareAdapter.MyViewHolder holder = new UsersShareAdapter.MyViewHolder(view);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(UsersShareAdapter.MyViewHolder holder, int position) {
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
        ImageView wpButton,emailButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            wpButton = itemView.findViewById(R.id.btnWp);
            emailButton=itemView.findViewById(R.id.btnEmail);

        }

        public void setData(final User user, int position) {

            final String message="I would like to invite you to the W'meet Event. Please check the event. ";

            boolean wpCheckExists =false;
            boolean emailCheckExists =false;
            this.nameTextView.setText(user.getDisplayName());
            Glide.with(userImageView)
                    .load(user.getProfilePictureURL())
                    .into(userImageView);
            wpButton.setClickable(true);


//            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(thisUser.getUserID());
            if(user.getEmail()==null){
                emailButton.setVisibility(View.GONE);
            }
            if (user.getPhoneNumber().equals("") || user.getPhoneNumber()==null){
                wpButton.setVisibility(View.GONE);
            }


            if(wpButton.isClickable()) {
                wpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendWpMesscage(message,user.getPhoneNumber());

                    }
                });
            }
            emailButton.setClickable(true);
            if(emailButton.isClickable()) {
                emailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendEmail(message,user.getEmail());
                        Toast.makeText(context,"Email Send", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }


        @Override
        public void onClick(View v) {

        }
    }
    public void sendWpMesscage(String message, String number){
        number = number.replace("+", "").replace(" ", "");
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.putExtra("jid", number + "@s.whatsapp.net");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setAction(Intent.ACTION_SEND);
        intent.setPackage("com.whatsapp");
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        context.startActivity(intent);
    }
    public void sendEmail(String message, String email){
        GMailSender.withAccount("computerproject3030@gmail.com", "BilgisayarProject1")
                .withTitle("W'meet event join code")
                .withBody(message)
                .withSender("GMailSenderLibrary")
                .toEmailAddress(email)
                .withListenner(new GmailListener() {
                    @Override
                    public void sendSuccess() {
                        Toast.makeText(context, "Email send", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void sendFail(String err) {
                        Toast.makeText(context, "Fail: " + err, Toast.LENGTH_SHORT).show();
                    }
                })
                .send();
    }
}
