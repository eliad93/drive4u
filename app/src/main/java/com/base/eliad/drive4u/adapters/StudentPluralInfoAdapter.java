package com.base.eliad.drive4u.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.chat.ChatMessageActivity;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;

import java.util.List;

public class StudentPluralInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = StudentPluralInfoAdapter.class.getName();
    public static final int MY_PERMISSIONS_REQUEST_CALL = 1989;

    private List<Student> students;
    private Teacher       mTeacher;
    private Activity      mActivity;

    public StudentPluralInfoAdapter(List<Student> item, Teacher teacher, Activity activity) {
        students = item;
        mTeacher = teacher;
        mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final int j = i;

        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_plural_info__item, viewGroup, false);

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Student student = students.get(j);
//                Intent intent = new Intent(v.getContext(), TeacherStudentInfoActivity.class);
//                intent.putExtra(TeacherBaseActivity.ARG_TEACHER, mTeacher);
//                intent.putExtra(StudentBaseActivity.ARG_STUDENT, student);
//                v.getContext().startActivity(intent);
//            }
//        });
        return new StudentPluralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Resources res = viewHolder.itemView.getResources();
        StudentPluralViewHolder holder = (StudentPluralViewHolder) viewHolder;
        final Student student = this.students.get(i);
        holder.setIsRecyclable(false);
        holder.textViewFirstName.setText(student.getFirstName());
        holder.textViewLastName.setText(student.getLastName());
        holder.textViewCity.setText(student.getCity());

        holder.imageViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ChatMessageActivity.class);
                intent.putExtra(ChatMessageActivity.ARG_CURRENT_USER, mTeacher);
                intent.putExtra(ChatMessageActivity.ARG_SECOND_USER, student);
                mActivity.startActivity(intent);
            }
        });

        holder.imageViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "textViewStudentPhoneNumber clicked");
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);  // or ACTION_CALL
                phoneIntent.setData(Uri.parse("tel:" + student.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "no permission to use call phone");
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL);
                    return;
                }
                v.getContext().startActivity(phoneIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentPluralViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewFirstName;
        public TextView textViewLastName;
        public TextView textViewCity;
        public ImageView imageViewPhone;
        public ImageView imageViewMessage;

        public StudentPluralViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFirstName = itemView.findViewById(R.id.StudentPluralInfoItemFirstName);
            textViewLastName  = itemView.findViewById(R.id.StudentPluralInfoItemLastName);
            textViewCity = itemView.findViewById(R.id.StudentPluralInfoItemCity);
            imageViewPhone = itemView.findViewById(R.id.imageViewPhone);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage);
        }
    }
}
