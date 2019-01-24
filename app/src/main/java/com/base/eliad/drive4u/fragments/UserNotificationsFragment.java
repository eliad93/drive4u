package com.base.eliad.drive4u.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.User;


public class UserNotificationsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    public UserNotificationsFragment() {
        // Required empty public constructor
    }


    public static UserNotificationsFragment newInstance(User user) {
        UserNotificationsFragment fragment = new UserNotificationsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.student_connection_request_notification_list_item,
                container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
