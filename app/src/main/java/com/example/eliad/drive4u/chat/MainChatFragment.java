package com.example.eliad.drive4u.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.TeacherUI.TeacherMainChatFragment;
import com.example.eliad.drive4u.adapters.ViewPagerAdapter;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;

public class MainChatFragment extends UserBaseFragment {

    private static final String TAG = TeacherMainChatFragment.class.getName();

    private ViewPager mViewPager;

    public void displayView(int viewId) {
        Log.d(TAG, "displayView");

        Fragment fragment = null;
        String msg = "BUG";
        switch (viewId) {
            case R.id.navigation_chats:
                msg = "Chats selected";
                mViewPager.setCurrentItem(0);
                break;
            case R.id.navigation_users:
                msg = "users selected";
                mViewPager.setCurrentItem(1);
                break;
        }

        Log.d(TAG, msg);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            displayView(item.getItemId());
            return true;
        }
    };

    public MainChatFragment() {
        // Required empty public constructor
    }

    public static MainChatFragment newInstance(User user) {
        MainChatFragment fragment = new MainChatFragment();
        Bundle args = newInstanceBaseArgs(user);
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
        View view =  inflater.inflate(R.layout.fragment_main_chat, container, false);
        Log.d(TAG, "onCreateView");

        BottomNavigationView navigation = (BottomNavigationView) view.findViewById(R.id.bottom_main_chat_navigation);
        mViewPager = view.findViewById(R.id.main_chat_frame);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(ChatsFragment.newInstance(mUser));
        viewPagerAdapter.addFragment(ChatUsersFragment.newInstance(mUser));

        mViewPager.setAdapter(viewPagerAdapter);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        navigation.
        displayView(R.id.navigation_chats);
        return view;
    }
}
