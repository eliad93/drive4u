package com.example.eliad.drive4u.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.activities.TeacherScheduleActivity;
import com.example.eliad.drive4u.models.Lesson;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;


public class TeacherScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Lesson.Status[] lessonsHours;
    private Context mContext;
    private ArrayList<LinkedList<Lesson>> allLessons;

    // Firebase
    private FirebaseFirestore db;

    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClickTeacherSchedule(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }


    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
                "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};

    public TeacherScheduleAdapter(Context context, Lesson.Status[] items, ArrayList<LinkedList<Lesson>> lessons ){
        lessonsHours = items;
        allLessons = lessons;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.teacher_dayview_schedule_hour_item, viewGroup, false);
            return new TeacherScheduleAdapter.TeacherScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            Resources res = viewHolder.itemView.getResources();
            final TeacherScheduleAdapter.TeacherScheduleViewHolder holder = (TeacherScheduleAdapter.TeacherScheduleViewHolder) viewHolder;
            String hour = this.hours[position];
            holder.setIsRecyclable(false);
            holder.lessonTime.setText(hour);
            if (lessonsHours[position] == Lesson.Status.S_CONFIRMED || lessonsHours[position] == Lesson.Status.T_CONFIRMED) {
                viewHolder.itemView.setClickable(false);
                viewHolder.itemView.setBackgroundColor(Color.GREEN);
                Lesson l = allLessons.get(position).getFirst();
                holder.startingLocationMessage.setText(R.string.starting_location + ": ");
                holder.endingLocationMessage.setText(R.string.ending_location + ": ");
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
                                    }
                                }
                            }
                        });

            } else {
                if (lessonsHours[position] == Lesson.Status.S_UPDATE || lessonsHours[position] == Lesson.Status.T_UPDATE || lessonsHours[position] == Lesson.Status.S_REQUEST) {
                    viewHolder.itemView.setBackgroundColor(Color.YELLOW);
                    viewHolder.itemView.setClickable(true);
                }else{
                    viewHolder.itemView.setClickable(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return hours.length-1;
        }

        public class TeacherScheduleViewHolder extends RecyclerView.ViewHolder {
            private TextView lessonTime;
            private TextView studentName;
            private TextView startingLocationMessage;
            private TextView startingLocation;
            private TextView endingLocationMessage;
            private TextView endingLocation;

            public TeacherScheduleViewHolder(View v) {
                super(v);
                lessonTime = (TextView) v.findViewById(R.id.lessonTime);
                studentName = (TextView) v.findViewById(R.id.studentName);
                startingLocationMessage = (TextView) v.findViewById(R.id.startingLocationMessage);
                startingLocation = (TextView) v.findViewById(R.id.startingLocation);
                endingLocationMessage = (TextView) v.findViewById(R.id.endingLocationMessage);
                endingLocation = (TextView) v.findViewById(R.id.endingLocation);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                mListener.onItemClickTeacherSchedule(position);
                            }
                        }
                    }
                });
            }

        }

    }


