package com.base.eliad.drive4u.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.notifications.APIService;
import com.base.eliad.drive4u.notifications.Client;
import com.base.eliad.drive4u.notifications.Data;
import com.base.eliad.drive4u.notifications.MyFirebaseMessaging;
import com.base.eliad.drive4u.notifications.MyResponse;
import com.base.eliad.drive4u.notifications.Sender;
import com.base.eliad.drive4u.notifications.Token;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.ChatMessageAdapter;
import com.base.eliad.drive4u.models.Chat;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessageActivity extends AppCompatActivity {

    private static final String TAG = ChatMessageActivity.class.getName();

    public static final String ARG_CURRENT_USER = TAG + ".arg_current_user";
    public static final String ARG_CURRENT_USER_ID = TAG + ".arg_current_user_id";
    public static final String ARG_SECOND_USER = TAG + ".arg_second_user";
    public static final String ARG_SECOND_USER_ID = TAG + ".arg_second_user_id";

    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String MESSAGE = "message";
    public static final String IS_SEEN = "seen";
    public static final String CHATS = "chats";
    public static final String PREFS = "PREFS";
    public static final String CURRENT_USER = "currentuser";
    public static final String NO_CURRENT_USER = "no_current_user";

    private CircleImageView profile_image;
    private TextView username;

    private User currUser;
    private User secUser;
    private Toolbar mToolbar;
    private Intent intent;

    private ImageButton btn_send;
    private EditText text_send;

    FirebaseFirestore db;
    FirebaseUser fuser;
    DatabaseReference reference;
    ChatMessageAdapter messageAdapter;
    List<Chat> mChats;
    RecyclerView recyclerView;

    APIService apiService;

    boolean notify = false ;

    ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mToolbar = findViewById(R.id.chat_message_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatMessageActivity.this,
                        MainChatActivity.class);
                intent.putExtra(MainChatActivity.ARG_USER, currUser);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.chat_message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image = findViewById(R.id.chat_message_toolbar_image);
        username = findViewById(R.id.chat_message_toolbar_username);
        btn_send = findViewById(R.id.chat_message_btn_send);
        text_send = findViewById(R.id.chat_message_text_send);

        initUsers();

        final String secUserName = secUser.getFirstName() + " " + secUser.getLastName();
        username.setText(secUserName);

        if (secUser.getImageUrl() == null || secUser.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(getApplicationContext()).load(secUser.getImageUrl()).into(profile_image);
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(currUser.getID(), secUser.getID(), msg);
                } else {
                    Toast.makeText(ChatMessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        readMessages(currUser.getID(), secUser.getID(), secUser.getImageUrl());

        seenMessage(secUser.getID());
    }

    private void initUsers() {
        Log.d(TAG, "initUsers");
        intent = getIntent();
        currUser = intent.getParcelableExtra(ARG_CURRENT_USER);
        secUser  = intent.getParcelableExtra(ARG_SECOND_USER);
        final String currUserId = fuser.getUid();
        final String secUserId = intent.getStringExtra(ARG_SECOND_USER_ID);

        if (currUser == null) {
            Log.d(TAG,"currUser is null");
            db.collection("Teachers")
                    .document(currUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    currUser = (User) snapshot.toObject(Teacher.class);
                                    Log.d(TAG,"Found a Teacher for CurrUser");
                                }
                            }
                        }
                    });
            db.collection("Students")
                    .document(currUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    currUser = snapshot.toObject(Student.class);
                                    Log.d(TAG,"Found a student for currUser");
                                }
                            }
                        }
                    });
        }

        if (secUser == null ) {
            Log.d(TAG,"SecUser is null");
            db.collection("Teachers")
                    .document(secUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG,"found a teacher for secUser");
                                    secUser = (User) snapshot.toObject(Teacher.class);
                                }
                            }
                        }
                    });
            db.collection("Students")
                    .document(secUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG,"found a student for secUser");
                                    currUser = snapshot.toObject(Student.class);
                                }
                            }
                        }
                    });
        }
    }

    private void seenMessage(String userid) {
        Log.d(TAG, "seenMessage");
        reference = FirebaseDatabase.getInstance().getReference(CHATS);
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currUser.getID()) &&
                            chat.getSender().equals(secUser.getID())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(IS_SEEN, true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "sendMessage");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(SENDER, sender);
        hashMap.put(RECEIVER, receiver);
        hashMap.put(MESSAGE, message);
        hashMap.put(IS_SEEN, false);

        reference.child(CHATS).push().setValue(hashMap);

        String userCollectionPath = currUser instanceof Student ? "Students" : "Teachers";
        final Class userClass = currUser instanceof Student ? Student.class : Teacher.class;
        final String msg = message;

        db.collection(userCollectionPath)
                .document(currUser.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null && snapshot.exists()) {
                                User user = (User) snapshot.toObject(userClass);
                                if (notify) {
                                    Log.d(TAG,"SENDING A NOTIFICATION!");
                                    sendNotification(receiver, user.getFullName(), msg);
                                }
                                notify = false;
                            }
                        }
                    }
                });
    }

    private void sendNotification(String receiver, final String senderName, final String message) {
        Log.d(TAG,"sendNotification");

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Token.TOKEN_PATH);
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(currUser.getID(), R.mipmap.ic_launcher,
                            senderName + ": " + message, MyFirebaseMessaging.NEW_MESSAGE_TITLE,
                            secUser.getID());

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChatMessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String currUserId, final String secUserId, final String imageURL) {
        Log.d(TAG,"readMessages");
        mChats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(currUserId) && chat.getSender().equals(secUserId) ||
                            chat.getReceiver().equals(secUserId) && chat.getSender().equals(currUserId)) {
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

    private void status(final String status) {
        currUser.setStatus(status);
        db.collection(getString(R.string.DB_Teachers))
                .document(currUser.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                db.collection(getString(R.string.DB_Teachers))
                                        .document(currUser.getID())
                                        .update("status", status);
                            } else {
                                db.collection(getString(R.string.DB_Students))
                                        .document(currUser.getID())
                                        .update("status", status);
                            }
                        }
                    }
                });
    }

    private void currentUser(String secUserId) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString(CURRENT_USER, secUserId);
        editor.apply();

    }
    @Override
    protected void onResume() {
        super.onResume();
        status(User.ONLINE);
        currentUser(secUser.getID());
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status(User.OFFLINE);
        currentUser(NO_CURRENT_USER);
    }
}
