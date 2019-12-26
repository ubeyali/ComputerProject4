package com.example.wemeet.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.wemeet.R;
import com.example.wemeet.adapters.ConversationAdapter;
import com.example.wemeet.classes.Conversation;
import com.example.wemeet.classes.Message;
import com.example.wemeet.classes.MyAppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends MyAppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private EditText contentTextView;
    private ConversationAdapter conversationAdapter;
    private Conversation conversation;
    private DocumentReference conversationReference;
    private DocumentReference senderRef;

    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String conversationID = getIntent().getStringExtra("conversationID");
        if(conversationID == null || conversationID.equals("")) finish();

        conversation = new Conversation();
        conversation.setConversationID(conversationID);
        messages = new ArrayList<>();

        senderRef = mDatabase.collection("users").document(mUser.getUid());

        conversationRecyclerView = findViewById(R.id.conversationRecyclerView);
        contentTextView = findViewById(R.id.contentTextView);

        conversationReference = mDatabase.collection("conversations").document(conversationID);
        conversationReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                conversation = documentSnapshot.toObject(Conversation.class);
                messages.clear();
                messages.addAll(conversation.getMessages());
                if(conversationAdapter == null) {
                    conversationAdapter = new ConversationAdapter(getApplicationContext(), messages, mUser.getUid());
                    conversationRecyclerView.setAdapter(conversationAdapter);
                } else {
                    conversationAdapter.notifyDataSetChanged();
                }

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
    }

    public void onSendButtonClick(View view) {
        String content = contentTextView.getText().toString();
        Message newMessage = new Message();
        newMessage.setContent(content);
        newMessage.setSenderRef(senderRef);
        conversationReference.update("messages", FieldValue.arrayUnion(newMessage));
        contentTextView.setText("");
    }
}
