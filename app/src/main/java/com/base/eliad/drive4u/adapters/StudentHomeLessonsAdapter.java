package com.base.eliad.drive4u.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Lesson;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/*
TODO:
show lessons ordered by date
 */
public class StudentHomeLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Lesson> lessonList;

    // Firebase
    private FirebaseFirestore db;

    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onEditButtonClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }

    public StudentHomeLessonsAdapter(List<Lesson> item) {
        lessonList = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_lesson_item, viewGroup, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        LessonViewHolder holder = (LessonViewHolder) viewHolder;
        Lesson lesson = this.lessonList.get(i);
        holder.setIsRecyclable(false);
        holder.textViewStatus.setText(lesson.getConformationStatus().getUserMessage());
        holder.textViewWhere.setText(lesson.getStartingLocation());
        String when = lesson.getDate() + " "  + lesson.getHour();
        holder.textViewWhen.setText(when);
        if(lesson.getConformationStatus() == Lesson.Status.T_CONFIRMED){
            viewHolder.itemView.setBackgroundColor(Color.GREEN);
            viewHolder.itemView.setClickable(false);
            ((LessonViewHolder) viewHolder).editButton.setVisibility(ImageButton.INVISIBLE);
        }else{
            if(lesson.getConformationStatus() == Lesson.Status.T_CANCELED){
                viewHolder.itemView.setBackgroundColor(Color.RED);
                ((LessonViewHolder) viewHolder).editButton.setVisibility(ImageButton.INVISIBLE);
            }else{
                viewHolder.itemView.setBackgroundColor(Color.YELLOW);
            }
        }
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWhen;
        private TextView textViewWhere;
        private TextView textViewStatus;
        private ImageButton editButton;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWhen = itemView.findViewById(R.id.textViewRecyclerViewLessonItemWhen);
            textViewWhere   = itemView.findViewById(R.id.textViewRecyclerViewLessonItemWhere);
            textViewStatus  = itemView.findViewById(R.id.textViewRecyclerViewLessonItemStatus);
            editButton = itemView.findViewById(R.id.imageButtonRecyclerViewLessonItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
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
                            mListener.onEditButtonClick(position);
                        }
                    }
                }
            });

        }
    }
}
