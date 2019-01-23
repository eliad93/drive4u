package com.base.eliad.drive4u.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Lesson;
import com.base.eliad.drive4u.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeacherHomeLessonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = TeacherHomeLessonsAdapter.class.getName();

    private List<Lesson> lessonList;
    private Context mContext;

    // Firebase
    private FirebaseFirestore db;

    private OnItemClickListener mListener;
    // interface for callback to get position
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mListener = onItemClickListener;
    }


    public TeacherHomeLessonsAdapter(Context context, List<Lesson> item) {
        lessonList = item;
        mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_lesson_item, viewGroup, false);
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
                                assert student != null;
                                holder.textViewName.setText(student.getFullName());
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
        if(lesson.getConformationStatus() == Lesson.Status.T_CONFIRMED){
            viewHolder.itemView.setBackgroundColor(Color.GREEN);
        }else{
            if(lesson.getConformationStatus() == Lesson.Status.S_CANCELED){
                viewHolder.itemView.setBackgroundColor(Color.RED);
            }
        }

    }

    @Override
    public int getItemCount() {
        if(lessonList == null){
            return 0;
        }
        return lessonList.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewWhen;
        public TextView textViewWhere;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewListItemTeacherLessonName);
            textViewWhen   = itemView.findViewById(R.id.textViewListItemTeacherLessonWhen);
            textViewWhere  = itemView.findViewById(R.id.textViewListItemTeacherLessonWhere);

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

        }
    }
}
