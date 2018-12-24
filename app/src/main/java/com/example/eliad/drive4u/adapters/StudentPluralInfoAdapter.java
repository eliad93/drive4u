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
import com.example.eliad.drive4u.activities.TeacherStudentInfoActivity;
import com.example.eliad.drive4u.base_activities.StudentBaseActivity;
import com.example.eliad.drive4u.base_activities.TeacherBaseActivity;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;

import java.util.List;

public class StudentPluralInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = StudentPluralInfoAdapter.class.getName();

    private List<Student> students;
    private Teacher       mTeacher;

    public StudentPluralInfoAdapter(List<Student> item, Teacher teacher) {
        students = item;
        mTeacher = teacher;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int j = i;

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_plural_info__item, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student student = students.get(j);
                Intent intent = new Intent(v.getContext(), TeacherStudentInfoActivity.class);
                intent.putExtra(TeacherBaseActivity.ARG_TEACHER, mTeacher);
                intent.putExtra(StudentBaseActivity.ARG_STUDENT, student);
                v.getContext().startActivity(intent);
            }
        });
        return new StudentPluralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Resources res = viewHolder.itemView.getResources();
        StudentPluralViewHolder holder = (StudentPluralViewHolder) viewHolder;
        Student student = this.students.get(i);
        holder.setIsRecyclable(false);
        holder.textViewFirstName.setText(student.getFirstName());
        holder.textViewLastName.setText(student.getLastName());
        holder.textViewCity.setText(student.getCity());
        holder.textViewPhone.setText(student.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentPluralViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFirstName;
        public TextView textViewLastName;
        public TextView textViewCity;
        public TextView textViewPhone;

        public StudentPluralViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFirstName = itemView.findViewById(R.id.StudentPluralInfoItemFirstName);
            textViewLastName  = itemView.findViewById(R.id.StudentPluralInfoItemLastName);
            textViewCity = itemView.findViewById(R.id.StudentPluralInfoItemCity);
            textViewPhone = itemView.findViewById(R.id.StudentPluralInfoItemPhone);
        }
    }
}
