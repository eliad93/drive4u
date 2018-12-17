package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.fragments.ChooseTeacherFragment;
import com.example.eliad.drive4u.models.Teacher;

import java.util.List;

public class TeacherSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Boolean mTeacherChoose;
    private List<Teacher> teachers;
    private ChooseTeacherFragment chooseTeacherFragment;

    public TeacherSearchAdapter(List<Teacher> items, Boolean teacherChoose){
        mTeacherChoose = teacherChoose;
        teachers = items;
        chooseTeacherFragment = new ChooseTeacherFragment();
//        Bundle
//        chooseTeacherFragment.setArguments();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_search_item, viewGroup, false);
        return new TeacherSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Resources res = viewHolder.itemView.getResources();
        TeacherSearchViewHolder holder = (TeacherSearchViewHolder) viewHolder;
        Teacher teacher = this.teachers.get(position);
        holder.setIsRecyclable(false);
        holder.mName.setText(teacher.getName());
        String numberOfStudentsText = res
                .getString(R.string.number_of_students, teacher.numberOfStudents());
        holder.mNumStudents.setText(numberOfStudentsText);
        if(mTeacherChoose){
            holder.mTeacherChoose.setOnClickListener(new NoTeacherOnClick());
        } else {
            holder.mTeacherChoose.setOnClickListener(new HasTeacherOnClick());
        }
    }

    private class NoTeacherOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
        }
    }

    private class HasTeacherOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Already connected to a teacher. please disconnect" +
                    "first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class TeacherSearchViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mName;
        public TextView mNumStudents;
        public ImageButton mTeacherChoose;

        public TeacherSearchViewHolder(View v){
            super(v);
            mImage = (ImageView)v.findViewById(R.id.imageViewTeacher);
            mName = (TextView)v.findViewById(R.id.textViewTeacherName);
            mNumStudents = (TextView)v.findViewById(R.id.textViewNumberOfStudents);
            mTeacherChoose = (ImageButton)v.findViewById(R.id.imageButtonChooseTeacher);
        }

    }
}
