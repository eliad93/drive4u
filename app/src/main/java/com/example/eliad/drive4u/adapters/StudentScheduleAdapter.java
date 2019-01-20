package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Lesson;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.List;

public class StudentScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mcontext;
    private List<Lesson> lessons;
    private List<String> hours;
    private int lessonLength;
    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClick(int position, List<Lesson> lessonsInHour);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }

    public StudentScheduleAdapter(Context context,List<String>  relevantHours,  List<Lesson> lessonList, int length){
        mcontext = context;
        lessons = lessonList;
        lessonLength = length;
        hours = relevantHours;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_dayview_hour_item, viewGroup, false);
        return new StudentScheduleAdapter.StudentScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Resources res = viewHolder.itemView.getResources();
        StudentScheduleAdapter.StudentScheduleViewHolder holder = (StudentScheduleAdapter.StudentScheduleViewHolder) viewHolder;
        holder.setIsRecyclable(false);
        String startingHour = hours.get(position);
        Lesson.Status lessonStatus = Lesson.Status.T_OPTION;
        for (Lesson l : lessons) {
            if (l.getHour().equals(hours.get(position))) {
                lessonStatus = lessons.get(position).getConformationStatus();
                break;
            }
        }
        holder.lessonTime.setText(startingHour + "-" + calculateTime(startingHour, lessonLength));
        if (lessonStatus == Lesson.Status.S_CONFIRMED || lessonStatus == Lesson.Status.S_UPDATE || lessonStatus == Lesson.Status.T_UPDATE || lessonStatus == Lesson.Status.S_REQUEST) {
            viewHolder.itemView.setBackgroundColor(mcontext.getResources().getColor(R.color.lessonHaveRequests));
        } else {
            viewHolder.itemView.setBackgroundColor(mcontext.getResources().getColor(R.color.lessonFree));
        }

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class StudentScheduleViewHolder extends RecyclerView.ViewHolder{
        public TextView lessonTime;

        public StudentScheduleViewHolder(View v){
            super(v);
            lessonTime = (TextView)v.findViewById(R.id.lessonTime);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        List<Lesson> lessonsInHour = new LinkedList<>();
                        for(Lesson l:lessons){
                            if(l.getHour().equals(hours.get(position))){
                                lessonsInHour.add(l);
                            }
                        }
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position, lessonsInHour);
                        }
                    }
                }
            });
        }

    }

    private String calculateTime(String hour, int addition) {
        String[] hourMinutes = hour.split(":");
        int time = Integer.parseInt(hourMinutes[0]) * 60 + Integer.parseInt(hourMinutes[1]) + addition;
        int newHour = time / 60;
        int newMinutes = time % 60;
        if (newMinutes < 10) {
            return String.valueOf(newHour) + ":" + "0" + String.valueOf(newMinutes);
        }
        return String.valueOf(newHour) + ":" + String.valueOf(newMinutes);
    }



}
