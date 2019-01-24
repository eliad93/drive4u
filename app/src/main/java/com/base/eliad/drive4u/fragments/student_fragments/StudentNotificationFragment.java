package com.base.eliad.drive4u.fragments.student_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.adapters.student_adapters.StudentNotificationsAdapter;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.UserAction;
import com.base.eliad.drive4u.student_ui.StudentBaseFragment;

import java.util.ArrayList;

public class StudentNotificationFragment extends StudentBaseFragment
        implements StudentNotificationsAdapter.OnInteraction {
    private static final String TAG = StudentNotificationFragment.class.getName();
    // RecyclerView items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // widgets
    private TextView textViewNoNotifications;

    public StudentNotificationFragment() {
        // Required empty public constructor
    }

    public static StudentNotificationFragment newInstance() {
        return new StudentNotificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_notification,
                container, false);
        initWidgets(view);
        return view;
    }

    private void initRecyclerView(View view) {
        Log.d(TAG, "in initRecyclerView");
        mRecyclerView = view.findViewById(R.id.recyclerViewStudentNotifications);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initWidgets(View view) {
        initRecyclerView(view);
        Log.d(TAG, "in initWidgets");
        textViewNoNotifications = view.findViewById(R.id.textViewStudentNotificationsNoNotifications);
        textViewNoNotifications.setVisibility(View.GONE);
        presentNotifications();
    }

    private void presentNotifications(){
        adapter = new StudentNotificationsAdapter(getContext(), mStudent,
                db, textViewNoNotifications);
        ((StudentNotificationsAdapter) adapter).setListener(StudentNotificationFragment.this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRemove(int position) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAdd(int position) {
        adapter.notifyDataSetChanged();
    }
}
