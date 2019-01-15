package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.ChatUsersAdapter;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherChatUsersFragment extends TeacherBaseFragment {

    private final static String TAG = TeacherChatUsersFragment.class.getName();

    private RecyclerView recyclerView;
    private ChatUsersAdapter usersAdapter;
    private List<User> mUsers;

    public TeacherChatUsersFragment() {
        // Required empty public constructor
    }

    public static TeacherChatUsersFragment newInstance(Teacher teacher) {
        TeacherChatUsersFragment fragment = new TeacherChatUsersFragment();
        Bundle args = newInstanceBaseArgs(teacher);
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

        mUsers = new ArrayList<>();

        readUsers();

        return view;
    }

    private void readUsers() {

        Log.d(TAG, "readUsers");
        db.collection(getString(R.string.DB_Students))
//                .whereEqualTo("teacherId", mTeacher.getID()) // TODO: for now present all the students
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
                                mUsers.add(student);
                            }
                            if (mUsers.size() > 0) {
                                usersAdapter = new ChatUsersAdapter(getContext(), mUsers, mTeacher);
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
