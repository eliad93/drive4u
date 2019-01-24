package com.base.eliad.drive4u.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.chat.ChatMessageActivity;
import com.base.eliad.drive4u.models.User;

import java.util.List;

public class ChatUsersAdapter extends RecyclerView.Adapter {

    private User mUser;
    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    public ChatUsersAdapter(Context context, List<User> users, User user, boolean ischat) {
        this.mUser = user;
        this.mContext = context;
        this.mUsers = users;
        this.ischat = ischat;
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

        if (user.getImageUrl() == null || user.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
        }

        if (ischat && user.getStatus() != null) {
            if (user.getStatus().equals(User.ONLINE)) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

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
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.chat_user_item_username);
            profile_image = itemView.findViewById(R.id.chat_user_item_profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
