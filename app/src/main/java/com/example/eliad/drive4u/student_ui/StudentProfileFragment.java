package com.example.eliad.drive4u.student_ui;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Student;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class StudentProfileFragment extends StudentBaseFragment {
    // Tag for the Log
    private static final String TAG = StudentProfileFragment.class.getName();

    CircleImageView image_profile;
    TextView username;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    // widgets
    private TextView textViewPhoneNumber, textViewEmail, textViewCity;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    public static StudentProfileFragment newInstance(Student student) {
        StudentProfileFragment fragment = new StudentProfileFragment();
        Bundle args = newInstanceBaseArgs(student);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_profile,
                container, false);

        // init text views
        image_profile           = view.findViewById(R.id.profile_image);
        username                = view.findViewById(R.id.profile_username);

        textViewPhoneNumber     = view.findViewById(R.id.StudentProfilePhone);
        textViewEmail           = view.findViewById(R.id.StudentProfileEmail);
        textViewCity            = view.findViewById(R.id.StudentProfileCity);

        // get ready for loading pictures
        storageReference        = FirebaseStorage.getInstance().getReference(User.UPLOADS);

        db.collection("Students")
                .document(mUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if ( e != null ) {
                            Log.d(TAG, "Listen failed: " + e);
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            mStudent = documentSnapshot.toObject(Student.class);
                            updateInfo();
                        }
                    }
                });
        // set the content
        updateInfo();

        return view;
    }

    private void updateInfo() {
        Log.d(TAG, "updateInfo");
        textViewPhoneNumber.setText(mStudent.getPhoneNumber());
        textViewEmail.setText(mStudent.getEmail());
        textViewCity.setText(mStudent.getCity());
        username.setText(mStudent.getFullName());

        if (mStudent.getImageUrl() == null || mStudent.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            image_profile.setImageResource(R.mipmap.ic_user_round);
        } else {
            Glide.with(getContext()).load(mStudent.getImageUrl()).into(image_profile);
        }

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        mStudent.setImageUrl(mUri);
                        db.collection(getString(R.string.DB_Students))
                                .document(mStudent.getID()).update("imageUrl", mUri);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Fail to write the picture!", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }


}
