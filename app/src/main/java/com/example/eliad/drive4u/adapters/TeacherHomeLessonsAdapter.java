package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.StudentHomeActivity;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeacherHomeLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = TeacherHomeLessonsAdapter.class.getName();
    private List<Lesson> lessonList;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public TeacherHomeLessonsAdapter(List<Lesson> item) {
        lessonList    = item;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int j = i;
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_home_lesson_item, viewGroup, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final LessonViewHolder holder = (LessonViewHolder) viewHolder;
        Lesson lesson = this.lessonList.get(i);
        holder.setIsRecyclable(false);
        db.collection("Students")
                .document(lesson.getStudentUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            if(document.exists()){
                                Student student = document.toObject(Student.class);
                                holder.textViewLastName.setText(student.getLastName());
                                holder.textViewFirsName.setText(student.getFirstName());
                                holder.mStudent = student; // TODO: we might not want this here!

                            } else {
                                Log.d(TAG, "No such student");
                            }
                        } else {
                            Log.d(TAG, "No such student");
                        }
                    }
                });
        holder.textViewWhere.setText(lesson.getStartingLocation());
        holder.textViewWhen.setText(lesson.getHour());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewWhere;
        public TextView textViewWhen;
        public TextView textViewFirsName;
        public TextView textViewLastName;

        public Student mStudent;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWhen   = itemView.findViewById(R.id.TeacherHomeLessonItemWhen);
            textViewWhere  = itemView.findViewById(R.id.TeacherHomeLessonItemLocation);
            textViewFirsName = itemView.findViewById(R.id.TeacherHomeLessonItemFirstName);
            textViewLastName = itemView.findViewById(R.id.TeacherHomeLessonItemLastName);
        }
    }
}
