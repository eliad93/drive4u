package com.base.eliad.drive4u.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.User;

import java.util.LinkedList;

public class TeacherConnectionRequestsAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Tag for the Log
    private final static String TAG = TeacherConnectionRequestsAdapter.class.getName();
    private Context context;
    private LinkedList<Student> students;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(int position, Student.ConnectionRequestStatus connectionRequestStatus);
    }

    public void setOnRequestListener(OnRequestClickListener OnRequestClickListener){
        Log.d(TAG, "in setOnItemClickListener");
        listener = OnRequestClickListener;
    }

    public TeacherConnectionRequestsAdapter(LinkedList<Student> mStudents, Context mContext){
        students = mStudents;
        context = mContext;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "in onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_simple_list_item_clickable, viewGroup, false);
        return new ConnectionRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d(TAG, "in onBindViewHolder");
        final ConnectionRequestViewHolder holder = (ConnectionRequestViewHolder) viewHolder;
        Student student = students.get(i);
        holder.setIsRecyclable(false);
        String profilePictureString = student.getImageUrl();
        if (profilePictureString == null || profilePictureString.equals(User.DEFAULT_IMAGE_KEY)) {
            holder.imageViewProfilePicture.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(profilePictureString).into(holder.imageViewProfilePicture);
        }
        holder.textViewName.setText(student.getFullName());
        holder.textViewCity.setText(String.format("City: %s", student.getCity()));
        holder.buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRequestClick(holder.getAdapterPosition(),
                        Student.ConnectionRequestStatus.ACCEPTED);
            }
        });
        holder.buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRequestClick(holder.getAdapterPosition(),
                        Student.ConnectionRequestStatus.DECLINED);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ConnectionRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewProfilePicture;
        TextView textViewName;
        TextView textViewCity;
        Button buttonAccept;
        Button buttonDecline;

        ConnectionRequestViewHolder(View v){
            super(v);
            imageViewProfilePicture = v.findViewById(R.id.imageViewStudentSimpleListItemPicture);
            textViewName = v.findViewById(R.id.textViewStudentSimpleListItemName);
            textViewCity = v.findViewById(R.id.textViewStudentSimpleListItemCity);
            buttonAccept = v.findViewById(R.id.buttonStudentSimpleListItemAccept);
            buttonDecline = v.findViewById(R.id.buttonStudentSimpleListItemDecline);
        }
    }
}
