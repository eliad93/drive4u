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

public class StudentScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Lesson.Status[] lessonsHours;
    private Context mcontext;

    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }

    private String[] hours = new String[]{"07:00", "08:00","09:00","10:00","11:00","12:00","13:00","14:00",
            "15:00","16:00","17:00","18:00","19:00","20:00", "21:00"};

    public StudentScheduleAdapter(Context context, Lesson.Status[] hoursStatus){
        lessonsHours = hoursStatus;
        mcontext = context;
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
        String hour = this.hours[position];
        holder.setIsRecyclable(false);
        holder.lessonTime.setText(hour);
        if (lessonsHours[position] == Lesson.Status.T_CONFIRMED) {
            viewHolder.itemView.setBackgroundColor(Color.GRAY);
        }else {
            if (lessonsHours[position] == Lesson.Status.S_CONFIRMED || lessonsHours[position] == Lesson.Status.S_UPDATE || lessonsHours[position] == Lesson.Status.T_UPDATE || lessonsHours[position] == Lesson.Status.S_REQUEST) {
                viewHolder.itemView.setBackgroundColor(Color.YELLOW);
            }
        }
    }

    @Override
    public int getItemCount() {
        return hours.length-1;
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
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }




}
