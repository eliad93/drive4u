package com.example.eliad.drive4u.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.fragments.ChooseTeacherFragment;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TeacherSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // Tag for the Log
    private final static String TAG = TeacherSearchAdapter.class.getName();

    private Context context;
    private Boolean mHasTeacher;
    private List<Teacher> teachers;
    private ChooseTeacherFragment chooseTeacherFragment;
    private Student mStudent;
    private DocumentReference mStudentDoc;
    private CollectionReference mTeachersDb;

    public TeacherSearchAdapter(List<Teacher> items, Student student,
                                DocumentReference studentDoc, CollectionReference teachersDb){
        assert mStudentDoc != null;
        assert teachersDb != null;
        mHasTeacher = (!student.getTeacherId().isEmpty());
        teachers = items;
        chooseTeacherFragment = new ChooseTeacherFragment();
        chooseTeacherFragment.setArguments(createArgsForFragment());
        mStudent = student;
        mStudentDoc = studentDoc;
        mTeachersDb = teachersDb;
    }

    private Bundle createArgsForFragment() {
        Bundle args = new Bundle();
        if(!mHasTeacher){
            args.putString("message", "connection request sent.");
        } else {
            args.putString("message", "Already has a teacher, disconnect first.");
        }
        return args;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_search_item, viewGroup, false);
        return new TeacherSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Resources res = viewHolder.itemView.getResources();
        TeacherSearchViewHolder holder = (TeacherSearchViewHolder) viewHolder;
        final Teacher teacher = this.teachers.get(position);
        holder.setIsRecyclable(false);
        holder.mName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        String numberOfStudentsText = res
                .getString(R.string.number_of_students, teacher.numberOfStudents());
        holder.mNumStudents.setText(numberOfStudentsText);
        holder.mTeacherChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTeacherFragment.show(((AppCompatActivity)context).getSupportFragmentManager(),
                        "student choose teacher");
                if(!mHasTeacher){
                    mTeachersDb.document(teacher.getID())
                            .update("students", FieldValue.arrayUnion(mStudent.getID()))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        teacher.addStudent(mStudent.getID());
                                        studentUpdateConnected(mStudent, teacher);
                                        mStudent.setTeacherId(teacher.getID());
                                        mStudent.setBalance(0);
                                    } else {

                                    }
                                }
                            });
                }
            }
        });
    }

    private void studentUpdateConnected(final Student mStudent, final Teacher teacher) {
        mStudentDoc.update("teacherId", teacher.getID())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mStudent.setTeacherId(teacher.getID());
                        } else {

                        }
                    }
                });
        mStudentDoc.update("balance", 0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mStudent.setBalance(0);
                        } else {

                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class TeacherSearchViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mName;
        public TextView mNumStudents;
        public ImageButton mTeacherChoose;

        public TeacherSearchViewHolder(View v){
            super(v);
            mImage = (ImageView)v.findViewById(R.id.imageViewTeacher);
            mName = (TextView)v.findViewById(R.id.textViewTeacherName);
            mNumStudents = (TextView)v.findViewById(R.id.textViewNumberOfStudents);
            mTeacherChoose = (ImageButton)v.findViewById(R.id.imageButtonChooseTeacher);
        }

    }
}
