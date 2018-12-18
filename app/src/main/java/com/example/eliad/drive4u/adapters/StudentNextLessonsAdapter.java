package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.PresentLessonActivity;
import com.example.eliad.drive4u.activities.StudentHomeActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;

import java.util.List;

/*
TODO:
show lessons ordered by date
 */
public class StudentNextLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = StudentNextLessonsAdapter.class.getName();
    public final static String LESSON_KEY = "com.example.eliad.drive4u.adapters.StudentNextLessonsAdapter.lesson";
    private List<Lesson> lessonList;

    public StudentNextLessonsAdapter(List<Lesson> item) {
        lessonList    = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int j = i;
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_next_lesson_item, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lesson lesson = lessonList.get(j);
                Intent intent = new Intent(v.getContext(), PresentLessonActivity.class);
//                intent.putExtra(LESSON_KEY, lesson); TODO: issue 55
                v.getContext().startActivity(intent);
            }
        });
        return new StudentNextLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Resources res =  viewHolder.itemView.getResources();
        StudentNextLessonViewHolder holder = (StudentNextLessonViewHolder) viewHolder;
        Lesson lesson = this.lessonList.get(i);
        holder.setIsRecyclable(false);
//        holder.textViewStatus.setText(lesson.getConformationStatus()); TODO
        holder.textViewWhere.setText(lesson.getStartingLocation());
        String when = lesson.getDate() + lesson.getHour();
        holder.textViewWhen.setText(when);

    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class StudentNextLessonViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewWhere;
        public TextView textViewWhen;
        public TextView textViewStatus;

        public StudentNextLessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStatus = itemView.findViewById(R.id.StudentNextLessonStatus);
            textViewWhen   = itemView.findViewById(R.id.StudentNextLessonWhen);
            textViewWhere  = itemView.findViewById(R.id.StudentNextLessonWhere);
        }
    }
}
