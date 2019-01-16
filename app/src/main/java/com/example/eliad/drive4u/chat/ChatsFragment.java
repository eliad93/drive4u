package com.example.eliad.drive4u.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.User;

public class ChatsFragment  extends UserBaseFragment {

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

        return view;
    }
}