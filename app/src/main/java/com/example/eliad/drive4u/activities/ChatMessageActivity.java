package com.example.eliad.drive4u.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
    }

    private void sendMessage(String sender, String receiver, String message) {
       DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SENDER, sender);
        hashMap.put(RECEIVER, receiver);
        hashMap.put(MESSAGE, message);

        reference.child(CHATS).push().setValue(hashMap);

    }
}
