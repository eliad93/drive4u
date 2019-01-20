package com.example.eliad.drive4u.teacher_ui;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eliad.drive4u.R;
import com.example.eliad.drive4u.models.Teacher;
import com.example.eliad.drive4u.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class TeacherProfileFragment extends TeacherBaseFragment {
    private static final String TAG = TeacherProfileFragment.class.getName();

    CircleImageView image_profile;
    TextView username;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    public static TeacherProfileFragment newInstance(Teacher teacher) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
        Bundle args = newInstanceBaseArgs(teacher);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.profile_username);

        storageReference = FirebaseStorage.getInstance().getReference(User.UPLOADS);

        if (mTeacher.getImageUrl() == null || mTeacher.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            image_profile.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(getContext()).load(mTeacher.getImageUrl()).into(image_profile);
        }

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
//        String text;
//        // init text views
//        TextView textViewFirstName = view.findViewById(R.id.TeacherProfileFirstName);
//        TextView textViewLastName = view.findViewById(R.id.TeacherProfileLastName);
//        TextView textViewPhoneNumber = view.findViewById(R.id.TeacherProfilePhone);
//        TextView textViewEmail = view.findViewById(R.id.TeacherProfileEmail);
//        TextView textViewCity = view.findViewById(R.id.TeacherProfileCity);
//        TextView textViewBalance = view.findViewById(R.id.TeacherProfileBalance);
//        TextView textViewCarModel = view.findViewById(R.id.TeacherProfileCarModel);
//        TextView textViewGearType = view.findViewById(R.id.TeacherProfileGearType);
//        TextView textViewPrice = view.findViewById(R.id.TeacherProfilePrice);
//        TextView textViewTotalPayed = view.findViewById(R.id.TeacherProfileTotalPayed);
//
//        textViewFirstName.setText(mTeacher.getFirstName());
//        textViewLastName.setText(mTeacher.getLastName());
//        textViewPhoneNumber.setText(mTeacher.getPhoneNumber());
//        textViewEmail.setText(mTeacher.getEmail());
//        textViewCity.setText(mTeacher.getCity());
//        text = Integer.toString(mTeacher.getBalance());
//        textViewBalance.setText(text);
//        textViewCarModel.setText(mTeacher.getCarModel());
//        textViewGearType.setText(mTeacher.getGearType());
//        text = Integer.toString(mTeacher.getPrice());
//        textViewPrice.setText(text);
//        text = Integer.toString(mTeacher.getTotalPayed());
//        textViewTotalPayed.setText(text);

        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
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

                        mTeacher.setImageUrl(mUri);
                        db.collection(getString(R.string.DB_Teachers))
                                .document(mTeacher.getID()).update("imageUrl", mUri);

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
}
