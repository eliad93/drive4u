package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.adapters.StudentPluralInfoAdapter;
import com.example.eliad.drive4u.built_in_utils.BorderLineDividerItemDecoration;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherStudentsFragment extends TeacherBaseFragment {

    private static final String TAG = TeacherStudentsFragment.class.getName();

    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public TeacherStudentsFragment() {
        // Required empty public constructor
    }

    public static TeacherStudentsFragment newInstance(Teacher teacher) {
        TeacherStudentsFragment fragment = new TeacherStudentsFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    private void presentStudents(View view) {
        Log.d(TAG, "presentStudents");
        db.collection(getString(R.string.DB_Students))
                .whereEqualTo("teacherId", mTeacher.getID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "presentStudents.onComplete");
                        if (task.isSuccessful()) {
                            LinkedList<Student> students = new LinkedList<>();
                            Log.d(TAG, "presentStudents.onComplete with result size " + task.getResult().size());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student student = document.toObject(Student.class);
                                Log.d(TAG, "current student by email: " + student.getEmail());
                                students.addLast(student);
                            }
                            if (students.size() > 0) {
                                mAdapter = new StudentPluralInfoAdapter(students, mTeacher);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: set recycler view here?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_students, container, false);

        initializeRecyclerView(view);
        presentStudents(view);
        return view;
    }

    private void initializeRecyclerView(View view) {
        Log.d(TAG, "in initializeRecyclerView");
        mRecyclerView         = view.findViewById(R.id.TeacherMyStudentsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new BorderLineDividerItemDecoration(view.getContext()));
    }

}
