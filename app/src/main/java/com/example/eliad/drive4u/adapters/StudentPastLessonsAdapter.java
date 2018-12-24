package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Lesson;

import java.util.List;

public class StudentPastLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Tag for the Log
    private final static String TAG = TeacherSearchAdapter.class.getName();
    private Context mContext;
    // lessons
    private List<Lesson> lessons;

    public StudentPastLessonsAdapter(List<Lesson> items, Context context){
        Log.d(TAG, "in StudentPastLessonsAdapter");
        lessons = items;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "in onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.student_past_lesson_item, viewGroup, false);
        return new StudentPastLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d(TAG, "in onBindViewHolder");
        Resources res = viewHolder.itemView.getResources();
        StudentPastLessonViewHolder holder = (StudentPastLessonViewHolder) viewHolder;
        Lesson lesson = this.lessons.get(i);
        holder.setIsRecyclable(false);
        holder.textViewLessonDate.setText(lesson.getDate());
        Lesson.Summary lessonSummary = lesson.getSummary();
        if(lessonSummary != null){
            holder.textViewLessonGrade.setText(lesson.getSummary().getGrade().ordinal());
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "in getItemCount");
        return lessons.size();
    }

    public class StudentPastLessonViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewLessonDate;
        public TextView textViewLessonGrade;
        public ConstraintLayout layout;
        public StudentPastLessonViewHolder(View v){
            super(v);
            textViewLessonDate = (TextView)v.findViewById(R.id.textViewPastLessonDate);
            textViewLessonGrade = (TextView)v.findViewById(R.id.textViewPastLessonGrade);
            layout = (ConstraintLayout)v.findViewById(R.id.constraintLayoutStudentPastLessonItem);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"WORKS!!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
