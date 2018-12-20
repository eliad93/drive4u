package com.example.eliad.drive4u.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Lesson;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Lesson.Status[] lessonsHours;
    private String teacherID;
    private String studentID;
    private Dialog lessonCreate;
    private Context mcontext;
    private String dateSelected;
    private TextView lessonDate;
    private TextView lessonStartingTime;
    private TextView lessonEndingTime;
    private TextView lessonStartingLocation;
    private TextView lessonEndingLocation;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
            "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};
    public StudentScheduleAdapter(Context context, Lesson.Status[] items, String dateSelected,String teacherID, String studentID ){
        lessonsHours = items;
        dateSelected = dateSelected;
        teacherID = teacherID;
        studentID = studentID;
        mcontext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int position = i;
        lessonCreate = new Dialog(mcontext);
        lessonCreate.setContentView(R.layout.student_schedule_lesson_dialog);

        lessonDate = (TextView) lessonCreate.findViewById(R.id.lesson_date);
        lessonStartingTime = (TextView) lessonCreate.findViewById(R.id.starting_time);
        lessonEndingTime = (TextView) lessonCreate.findViewById(R.id.ending_time);
        lessonStartingLocation = (TextView) lessonCreate.findViewById(R.id.starting_location);
        lessonEndingLocation = (TextView) lessonCreate.findViewById(R.id.ending_location);

        Button submit = (Button) lessonCreate.findViewById(R.id.add_lesson);


        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_dayview_hour_item, viewGroup, false);
        if (lessonsHours[i] == Lesson.Status.S_CONFIRMED || lessonsHours[i] == Lesson.Status.T_CONFIRMED) {
            view.setClickable(false);
            view.setBackgroundColor(Color.RED);
        }else {
            if (lessonsHours[i] == Lesson.Status.S_UPDATE || lessonsHours[i] == Lesson.Status.S_UPDATE || lessonsHours[i] == Lesson.Status.S_REQUEST) {
                view.setBackgroundColor(Color.YELLOW);
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        view.setBackgroundColor(Color.GREEN);
                                        view.setSelected(true);

                                        lessonDate.setText(dateSelected);
                                        lessonStartingTime.setText(hours[position]);
                                        lessonEndingTime.setText(hours[position+1]);

                                        lessonCreate.show();

                                    }
                                }
        );
        return new StudentScheduleAdapter.StudentScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Resources res = viewHolder.itemView.getResources();
        StudentScheduleAdapter.StudentScheduleViewHolder holder = (StudentScheduleAdapter.StudentScheduleViewHolder) viewHolder;
        String hour = this.hours[position];
        holder.setIsRecyclable(false);
        holder.lessonTime.setText(hour);
        if (lessonsHours[position] == Lesson.Status.S_CONFIRMED || lessonsHours[position] == Lesson.Status.T_CONFIRMED) {
            holder.lessonTime.setBackgroundColor(Color.RED);
        }else {
            if (lessonsHours[position] == Lesson.Status.S_UPDATE || lessonsHours[position] == Lesson.Status.S_UPDATE || lessonsHours[position] == Lesson.Status.S_REQUEST) {
                holder.lessonTime.setBackgroundColor(Color.YELLOW);
            }
        }
    }

    @Override
    public int getItemCount() {
        return hours.length;
    }

    public static class StudentScheduleViewHolder extends RecyclerView.ViewHolder{
        public TextView lessonTime;

        public StudentScheduleViewHolder(View v){
            super(v);
            lessonTime = (TextView)v.findViewById(R.id.lessonTime);
        }

    }

    public void addLesson(View v){
        Lesson lesson = new Lesson( teacherID, studentID, dateSelected,lessonStartingTime.getText().toString(),
                lessonStartingLocation.getText().toString(),lessonEndingLocation.getText().toString(), Lesson.Status.S_REQUEST);

        db.collection("Lessons").add(lesson);
        lessonCreate.hide();

    }


}
