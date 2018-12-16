package com.example.eliad.drive4u;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TeacherSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Teacher> teachers;
    private Context context;

    public TeacherSearchAdapter(List<Teacher> items){
        teachers = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.context);
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
                .getString(R.string.number_of_students, teacher.getNumberOfStudents());
        holder.mNumStudents.setText(numberOfStudentsText);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class TeacherSearchViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mName;
        public TextView mNumStudents;
        public ImageButton mChooseTeacher;

        public TeacherSearchViewHolder(View v){
            super(v);
            mImage = (ImageView)v.findViewById(R.id.imageViewTeacher);
            mName = (TextView)v.findViewById(R.id.textViewTeacherName);
            mNumStudents = (TextView)v.findViewById(R.id.textViewNumberOfStudents);
            mChooseTeacher = (ImageButton)v.findViewById(R.id.imageButtonChooseTeacher);
        }

    }
}
