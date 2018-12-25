package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;

public class TeacherChooseStudentLessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LinkedList<Lesson> lessons;
    private int hour;
    // Firebase
    private FirebaseFirestore db;

    private OnItemClickListener mListener;

    // interface for callback to get position
    public interface OnItemClickListener {
        void onItemClickRequestsList(int position, int hour);

        void onEditButtonClickRequestsList(int position, int hour);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public TeacherChooseStudentLessonAdapter(Context context, LinkedList<Lesson> items, int position) {
        lessons = items;
        mContext = context;
        hour = position;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_students_requests_item, viewGroup, false);
        return new TeacherChooseStudentLessonAdapter.TeacherChooseStudentLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final TeacherChooseStudentLessonAdapter.TeacherChooseStudentLessonViewHolder holder = (TeacherChooseStudentLessonAdapter.TeacherChooseStudentLessonViewHolder) viewHolder;
        Lesson l = lessons.get(position);
        Lesson.Status lessonStatus = l.getConformationStatus();
        holder.setIsRecyclable(false);
        holder.startingLocation.setText(l.getStartingLocation());
        holder.endingLocation.setText(l.getEndingLocation());
        db.collection("Students").whereEqualTo("id", l.getStudentUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Student s = document.toObject(Student.class);
                                holder.studentName.setText(s.getFirstName() + " " + s.getLastName());
                                holder.studentNumberLessons.setText(String.valueOf(s.getNumberOfLessons()));
                            }
                        }
                    }
                });
        if (lessonStatus == Lesson.Status.S_CONFIRMED) {
            viewHolder.itemView.setBackgroundColor(Color.GREEN);
        } else {
            if (lessonStatus == Lesson.Status.S_UPDATE || lessonStatus == Lesson.Status.T_UPDATE) {
                viewHolder.itemView.setBackgroundColor(Color.YELLOW);
            } else {
                if (lessonStatus == Lesson.Status.S_REQUEST) {
                    viewHolder.itemView.setBackgroundColor(Color.CYAN);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public class TeacherChooseStudentLessonViewHolder extends RecyclerView.ViewHolder {
        private TextView studentName;
        private TextView studentNumberLessons;
        private TextView startingLocation;
        private TextView endingLocation;
        private ImageButton editButton;

        public TeacherChooseStudentLessonViewHolder(View v) {
            super(v);
            studentName = (TextView) v.findViewById(R.id.studentName);
            studentNumberLessons = (TextView) v.findViewById(R.id.studentNumberLessons);
            startingLocation = (TextView) v.findViewById(R.id.startingLocation);
            endingLocation = (TextView) v.findViewById(R.id.endingLocation);
            editButton = (ImageButton) v.findViewById(R.id.editButton);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClickRequestsList(position, hour);
                        }
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onEditButtonClickRequestsList(position, hour);
                        }
                    }
                }

            });

        }
    }
}
