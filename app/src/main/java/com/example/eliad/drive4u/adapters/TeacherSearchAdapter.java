package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;

import java.util.List;

public class TeacherSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // Tag for the Log
    private final static String TAG = TeacherSearchAdapter.class.getName();
    private Context mContext;
    private List<Teacher> teachers;
    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        Log.d(TAG, "in setOnItemClickListener");
        mListener = onItemClickListener;
    }

    public TeacherSearchAdapter(List<Teacher> items, Context context){
        Log.d(TAG, "in TeacherSearchAdapter");
        teachers = items;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "in onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.teacher_search_item, viewGroup, false);
        return new TeacherSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        Log.d(TAG, "in onBindViewHolder");
        Resources res = viewHolder.itemView.getResources();
        final TeacherSearchViewHolder holder = (TeacherSearchViewHolder) viewHolder;
        final Teacher teacher = this.teachers.get(position);
        holder.setIsRecyclable(false);
        holder.mName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        holder.mCity.setText("City: " + teacher.getCity());
        String numberOfStudentsText = res
                .getString(R.string.number_of_students, teacher.numberOfStudents());
        holder.mNumStudents.setText(numberOfStudentsText);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "in getItemCount");
        return teachers.size();
    }

    public class TeacherSearchViewHolder extends RecyclerView.ViewHolder{
        public TextView mName;
        public TextView mCity;
        public TextView mNumStudents;
        public ImageButton mTeacherChoose;

        public TeacherSearchViewHolder(View v){
            super(v);
            mName = (TextView)v.findViewById(R.id.textViewTeacherName);
            mCity = (TextView)v.findViewById(R.id.textViewTeacherCity);
            mNumStudents = (TextView)v.findViewById(R.id.textViewNumberOfStudents);
            mTeacherChoose = (ImageButton)v.findViewById(R.id.imageButtonChooseTeacher);
            mTeacherChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
