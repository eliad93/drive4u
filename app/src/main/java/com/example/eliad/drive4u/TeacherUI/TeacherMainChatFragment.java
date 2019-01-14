package com.example.eliad.drive4u.TeacherUI;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.chat.ChatsFragment;
import com.example.eliad.drive4u.chat.UsersFragment;
import com.example.eliad.drive4u.models.Teacher;

public class TeacherMainChatFragment extends TeacherBaseFragment {

    private static final String TAG = TeacherMainChatFragment.class.getName();

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");

        Fragment fragment = null;
        String msg = "BUG";
        switch (viewId) {
            case R.id.navigation_chats:
                msg = "Chats selected";
                fragment = ChatsFragment.newInstance();
                break;
            case R.id.navigation_users:
                msg = "users selected";
                fragment = UsersFragment.newInstance();
                break;
        }

        Log.d(TAG, msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

        if (fragment != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.main_chat_frame, fragment);
            ft.commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayView(item.getItemId());
            return true;
        }
    };

    public TeacherMainChatFragment() {
        // Required empty public constructor
    }

    public static TeacherMainChatFragment newInstance(Teacher teacher) {
        TeacherMainChatFragment fragment = new TeacherMainChatFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_teacher_main_chat, container, false);
        Log.d(TAG, "onCreateView");

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.teacher_chat_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        displayView(R.id.navigation_chats);
        return view;
    }


}
