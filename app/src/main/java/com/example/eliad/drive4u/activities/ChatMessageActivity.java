package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ChatMessageAdapter;
import com.example.eliad.drive4u.models.Chat;
import com.example.eliad.drive4u.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity {

    private static final String TAG = ChatMessageActivity.class.getName();

    public static final String ARG_CURRENT_USER = TAG + ".arg_current_user";
    public static final String ARG_SECOND_USER = TAG + ".arg_second_user";

    private static final String SENDER = "sender";
    private static final String RECEIVER = "receiver";
    private static final String MESSAGE = "message";
    private static final String CHATS = "chats";

    private CircleImageView profile_image;
    private TextView username;

    private User currUser;
    private User secUser;
    private Toolbar mToolbar;
    private Intent intent;

    private ImageButton btn_send;
    private EditText text_send;

    DatabaseReference reference;
    ChatMessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        mToolbar = findViewById(R.id.chat_message_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.chat_message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image = findViewById(R.id.chat_message_toolbar_image);
        username = findViewById(R.id.chat_message_toolbar_username);
        btn_send = findViewById(R.id.chat_message_btn_send);
        text_send = findViewById(R.id.chat_message_text_send);

        intent = getIntent();

        currUser = intent.getParcelableExtra(ARG_CURRENT_USER);
        secUser  = intent.getParcelableExtra(ARG_SECOND_USER);

        final String secUserName = secUser.getFirstName() + " " + secUser.getLastName();
        username.setText(secUserName);
        profile_image.setImageResource(R.mipmap.ic_launcher);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(currUser.getID(), secUser.getID(), msg);
                } else {
                    Toast.makeText(ChatMessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
        /*
        TODO: Eliad add image URL here (better even if it is on a event listener for pic updates.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChanged(DataSnapshot dataSnapshot) {
                if (user.getImageURL().equals("default") {
                     set ic launcher
                } else {
                    load image from url and set it to the profile_image
                }

                readMessage(currUser.getID(), secUser.getID(), secUser.getImageURL());
            }
        }
         */
        readMessages(currUser.getID(), secUser.getID(), "");
    }

    private void sendMessage(String sender, String receiver, String message) {
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SENDER, sender);
        hashMap.put(RECEIVER, receiver);
        hashMap.put(MESSAGE, message);

        reference.child(CHATS).push().setValue(hashMap);

    }

    private void readMessages(final String myid, final String userid, final String imageURL) {
        mChats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mChats.add(chat);
                    }
                }
                messageAdapter = new ChatMessageAdapter(ChatMessageActivity.this, mChats, imageURL);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
