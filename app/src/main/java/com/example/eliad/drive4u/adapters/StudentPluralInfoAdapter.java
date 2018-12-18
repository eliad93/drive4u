package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;

import java.util.List;

public class StudentPluralInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = StudentPluralInfoAdapter.class.getName();

    private List<Student> students;

    public StudentPluralInfoAdapter(List<Student> item) {
        students = item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_plural_info__item, viewGroup, false);
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
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentPluralViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFirstName;
        public TextView textViewLastName;
        public TextView textViewCity;

        public StudentPluralViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFirstName = itemView.findViewById(R.id.textViewFirstName);
            textViewLastName  = itemView.findViewById(R.id.textViewLastName);
            textViewCity = itemView.findViewById(R.id.textViewCity);
        }
    }
}
