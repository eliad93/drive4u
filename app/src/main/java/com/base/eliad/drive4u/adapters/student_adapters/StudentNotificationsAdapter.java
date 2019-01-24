package com.base.eliad.drive4u.adapters.student_adapters;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class StudentNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // Tag for the Log
    private final static String TAG = StudentNotificationsAdapter.class.getName();
    private Context context;
    private ArrayList<UserAction> actions = new ArrayList<>();
    private Student student;
    // firebase
    private ArrayList<ListenerRegistration> registrations = new ArrayList<>();
    private FirebaseFirestore db;
    // external widgets
    private TextView textViewNoNotifications;
    private OnInteraction callBack;

    public interface OnInteraction{
        public void onRemove(int i);
        public void onAdd(int position);
    }

    public void setListener(OnInteraction onInteraction){
        callBack = onInteraction;
    }

    public StudentNotificationsAdapter(Context mContext, Student mStudent,
                                       FirebaseFirestore mDb, TextView mTextViewNoNotifications){
        context = mContext;
        student = mStudent;
        db = mDb;
        textViewNoNotifications = mTextViewNoNotifications;
        initData();
    }

    private void initData() {
        initNotifications();
    }

    private void initNotifications() {
        initConnectionRequest();
    }

    private void initConnectionRequest() {
        final Query query = db.collection(context.getString(R.string.students_actions_history))
                .whereEqualTo("receiverId", student.getID());
        final Query query1 = query.whereEqualTo("notice", UserAction.Notice.UNSEEN.getMessage());
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    UserAction userAction = documentSnapshot.toObject(UserAction.class);
                    userAction.setActionId(documentSnapshot.getId());
                    if(!actions.contains(userAction)){
                        actions.add(userAction);
                        notifyDataSetChanged();
//                        callBack.onAdd(actions.indexOf(userAction));
                    }
                }
                registerToChanges(query1);
            }
        });
        final Query query2 = query.whereEqualTo("notice", UserAction.Notice.SEEN.getMessage());
        query2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    UserAction userAction = documentSnapshot.toObject(UserAction.class);
                    userAction.setActionId(documentSnapshot.getId());
                    if(!actions.contains(userAction)){
                        actions.add(userAction);
                        notifyDataSetChanged();
//                        callBack.onAdd(actions.indexOf(userAction));
                    }
                }
                registerToChanges(query1);
            }
        });
    }

    private void registerToChanges(Query query) {
        registrations.add(query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    userAction.setActionId(doc.getId());
                    if(!actions.contains(userAction)){
                        actions.add(userAction);
                        notifyDataSetChanged();
//                        callBack.onAdd(actions.indexOf(userAction));
                    }
                }
            }
        }));
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
        final UserAction action = this.actions.get(i);
        if(UserAction.Type.CONNECTION_REQUEST.getMessage().equals(action.getType())){
            bindConnectionRequest(action,
                    (StudentConnectionRequestViewHolder)viewHolder);
        }
    }

    private void bindConnectionRequest(final UserAction action,
                                       @NonNull final StudentConnectionRequestViewHolder holder) {
        db.collection(context.getString(R.string.DB_Teachers))
                .document(action.getSenderId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Teacher teacher = documentSnapshot.toObject(Teacher.class);
                if(teacher != null){
                    finalizeConnectionRequestBind(teacher, holder, action);
                }
            }
        });
    }

    private void finalizeConnectionRequestBind(Teacher teacher, StudentConnectionRequestViewHolder
            holder, final UserAction action) {
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
                db.collection(context.getString(R.string.students_actions_history))
                        .document(action.getActionId())
                        .update("notice", UserAction.Notice.RESPONDED.getMessage())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                actions.remove(action);
                                notifyDataSetChanged();
//                                callBack.onRemove(actions.indexOf(action));
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
        for (ListenerRegistration listenerRegistration: registrations) {
            listenerRegistration.remove();
        }
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
