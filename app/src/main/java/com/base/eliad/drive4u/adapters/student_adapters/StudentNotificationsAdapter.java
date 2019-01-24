package com.base.eliad.drive4u.adapters.student_adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.Teacher;
import com.base.eliad.drive4u.models.User;
import com.base.eliad.drive4u.models.UserAction;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.Nullable;

public class StudentNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // Tag for the Log
    private final static String TAG = StudentNotificationsAdapter.class.getName();
    private Context context;
    private ArrayList<UserAction> actions = new ArrayList<>();
    private Student student;
    private Teacher teacher;
    // firebase
    private ListenerRegistration registration;
    private FirebaseFirestore db;
    // external widgets
    TextView textViewNoNotifications;

    public StudentNotificationsAdapter(Context mContext, Student mStudent,
                                       FirebaseFirestore mDb, TextView mTextViewNoNotifications){
        context = mContext;
        student = mStudent;
        db = mDb;
        textViewNoNotifications = mTextViewNoNotifications;
        initData();
    }

    private void initData() {
        db.collection(context.getString(R.string.DB_Teachers))
                .document(student.getTeacherId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                teacher = documentSnapshot.toObject(Teacher.class);
                initNotifications();
            }
        });
    }

    private void initNotifications() {
        db.collection(context.getString(R.string.students_actions_history))
                .whereEqualTo("receiverId", student.getID())
                .whereEqualTo("notice", UserAction.Notice.UNSEEN.getMessage())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                actions.addAll(queryDocumentSnapshots.toObjects(UserAction.class));
                registerToChanges();
            }
        });
    }

    private void registerToChanges() {
        Query query = db.collection(context.getString(R.string.students_actions_history))
                .whereEqualTo("receiverId", student.getID())
                .whereEqualTo("notice", UserAction.Notice.UNSEEN.getMessage());
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(queryDocumentSnapshots == null){
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    UserAction userAction = doc.toObject(UserAction.class);
                    if(!actions.contains(userAction)){
                        actions.add(userAction);
                        notifyItemInserted(actions.indexOf(userAction));
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "in onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_connection_request_notification_list_item,
                viewGroup, false);
        return new StudentConnectionRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d(TAG, "in onBindViewHolder");
        StudentConnectionRequestViewHolder holder = (StudentConnectionRequestViewHolder) viewHolder;
        final UserAction action = this.actions.get(i);
        String teacherImageUrl = teacher.getImageUrl();
        if (teacherImageUrl == null || teacherImageUrl.equals(User.DEFAULT_IMAGE_KEY)) {
            holder.imageViewTeacher.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(teacherImageUrl).into(holder.imageViewTeacher);
        }
        holder.setIsRecyclable(false);
        holder.textViewName.setText(teacher.getFullName());
        holder.textViewResponse.setText(action.getDescription());
        holder.buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index = actions.indexOf(action);
                db.collection(context.getString(R.string.students_actions_history))
                        .document(action.getActionId())
                        .update("notice", UserAction.Notice.UNSEEN.getMessage())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                actions.remove(action);
                                notifyItemRemoved(index);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "in getItemCount");
        if(textViewNoNotifications != null){
            if(actions.size() > 0){
                textViewNoNotifications.setVisibility(View.GONE);
            } else {
                textViewNoNotifications.setVisibility(View.VISIBLE);
            }
        }
        return actions.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        registration.remove();
    }

    public class StudentConnectionRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewTeacher;
        TextView textViewName;
        TextView textViewResponse;
        Button buttonDismiss;

        StudentConnectionRequestViewHolder(View v){
            super(v);
            imageViewTeacher = v.findViewById(R.id.imageViewStudentConnectionRequestNotificationListItem);
            textViewName = v.findViewById(R.id.textViewStudentConnectionRequestNotificationListItemName);
            textViewResponse = v.findViewById(R.id.textViewStudentConnectionRequestNotificationListItemResponse);
            buttonDismiss = v.findViewById(R.id.buttonStudentConnectionRequestNotificationListItemDismiss);
        }
    }
}
