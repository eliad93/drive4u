package com.example.eliad.drive4u.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.chat.ChatMessageActivity;
import com.example.eliad.drive4u.models.User;

import java.util.List;

public class ChatUsersAdapter extends RecyclerView.Adapter {

    private User mUser;
    private Context mContext;
    private List<User> mUsers;

    public ChatUsersAdapter(Context context, List<User> users, User user) {
        this.mUser = user;
        this.mContext = context;
        this.mUsers = users;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_user_item, viewGroup, false);
        return new ChatUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatUsersAdapter.ViewHolder holder = (ViewHolder) viewHolder;

        final User user = mUsers.get(i);
        String username = user.getFirstName() + " " + user.getLastName();

        holder.username.setText(username);

        // TODO: Eliad set the users image!
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        Glide.with(mContext).load(userImageURL()).into(viewHolder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatMessageActivity.class);

                intent.putExtra(ChatMessageActivity.ARG_CURRENT_USER, mUser);
                intent.putExtra(ChatMessageActivity.ARG_SECOND_USER, user);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.chat_user_item_username);
            profile_image = itemView.findViewById(R.id.chat_user_item_profile_image);
        }
    }
}
