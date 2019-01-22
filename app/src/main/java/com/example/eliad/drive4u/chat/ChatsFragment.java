package com.example.eliad.drive4u.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.notifications.Token;
import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ChatUsersAdapter;
import com.example.eliad.drive4u.models.Chat;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment  extends UserBaseFragment {

    private static final String TAG = ChatsFragment.class.getName();

    private RecyclerView recyclerView;

    private ChatUsersAdapter usersAdapter;
    private List<User> mUsersList;

    DatabaseReference reference;

    private List<String> usersList;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(User user) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = newInstanceBaseArgs(user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference(ChatMessageActivity.CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    // if this user sent the message we want to present him in the page
                    if (chat.getSender().equals(mUser.getID())) {
                        usersList.add(chat.getReceiver());
                    }
                    // or if this user received the message we want to present him in the page
                    if (chat.getReceiver().equals(mUser.getID())) {
                        usersList.add(chat.getSender());
                    }
                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Token.TOKEN_PATH);
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void readChats() {
        mUsersList = new ArrayList<>();

        final String classRoom = mUser.getClassRoom();

        Activity activity = getActivity();
        if (activity == null || !isAdded()) {
            Log.d(TAG, "Fragment not attached to a activity");
            return;
        }

        if (!classRoom.equals(mUser.getID()) && usersList.contains(classRoom)) {
            // this is a student (the class room is the teachers id), and the teacher is in the usersList.
            db.collection(getResources().getString(R.string.DB_Teachers))
                    .document(classRoom)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && task.getResult().exists()) {
                                    Teacher t = task.getResult().toObject(Teacher.class);
                                    mUsersList.add(t);
                                }
                            }
                        }
                    });
        }
        db.collection(getString(R.string.DB_Students))
//                .whereEqualTo("teacherId", classRoom) //TODO: for debugging Ill show all students
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "readUsers.onComplete");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "presentStudents.onComplete with result size " + task.getResult().size());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student student = document.toObject(Student.class);
                                Log.d(TAG, "current student by email: " + student.getEmail());

                                // we don't want the user to message himself
                                if (mUser.getID().equals(student.getID())) {
                                    continue;
                                }

                                // there are some messages between the current user and the student
                                // and the student is not in the list yet.
                                if (usersList.contains(student.getID()) &&
                                        !mUsersList.contains(student)) {
                                    mUsersList.add(student);
                                }
                            }
                            if (mUsersList.size() > 0) {
                                usersAdapter = new ChatUsersAdapter(getContext(), mUsersList, mUser, true);
                                recyclerView.setAdapter(usersAdapter);
                            } else {
                                Log.d(TAG, "This Teacher has no students");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}