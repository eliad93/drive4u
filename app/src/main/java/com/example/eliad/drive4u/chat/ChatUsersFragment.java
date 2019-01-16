package com.example.eliad.drive4u.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ChatUsersAdapter;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatUsersFragment extends UserBaseFragment {

    private final static String TAG = ChatUsersFragment.class.getName();

    private RecyclerView recyclerView;
    private ChatUsersAdapter usersAdapter;
    private List<User> mUsersList;

    public ChatUsersFragment() {
        // Required empty public constructor
    }

    public static ChatUsersFragment newInstance(User user) {
        ChatUsersFragment fragment = new ChatUsersFragment();
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
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chat_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        readUsers();

        return view;
    }

    private void readUsers() {
        Log.d(TAG, "readUsers");
        mUsersList = new ArrayList<>();

        final String classRoom = mUser.getClassRoom();


        if (classRoom.equals("")) {
            // this is a student with no teacher. we can let him message all teachers, or nobody.
            Toast.makeText(getContext(), "Choose a teacher before trying to chat", Toast.LENGTH_SHORT).show();
//            return; TODO: block students with no teachers
        } else if (!classRoom.equals(mUser.getID())) {
            // this is a student, add his teacher to the list.
            DocumentSnapshot document = db.collection(getString(R.string.DB_Teachers))
                    .document(classRoom)
                    .get()
                    .getResult();
            if (document != null && document.exists()) {
                Teacher t = document.toObject(Teacher.class);
                mUsersList.add(t);
            }
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
                                if (!mUser.getID().equals(student.getID())) {
                                    mUsersList.add(student);
                                }
                            }
                            if (mUsersList.size() > 0) {
                                usersAdapter = new ChatUsersAdapter(getContext(), mUsersList, mUser, false);
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
