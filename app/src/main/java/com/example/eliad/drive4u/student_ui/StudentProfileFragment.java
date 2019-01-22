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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class StudentProfileFragment extends StudentBaseFragment {
    // Tag for the Log
    private static final String TAG = StudentProfileFragment.class.getName();

    CircleImageView image_profile;
    TextView username;
    private ImageButton editBtn;
    private TextView textViewPhoneNumber, textViewEmail, textViewCity;

    private EditText editTextName, editTextPhone, editTextCity;

    private String name, firstName="", lastName="", phone, city;

    private boolean isEditMode;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Contract("_ -> new")
    public static StudentProfileFragment newInstance(Student student) {
        return new StudentProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_student_profile,
                container, false);
        // init text views
        image_profile           = view.findViewById(R.id.profile_image);
        username                = view.findViewById(R.id.profile_username);
        isEditMode = false;
        editBtn = view.findViewById(R.id.student_imageButtonEdit);
        textViewPhoneNumber     = view.findViewById(R.id.StudentProfilePhone);
        textViewEmail           = view.findViewById(R.id.StudentProfileEmail);
        textViewCity            = view.findViewById(R.id.StudentProfileCity);
        // get ready for loading pictures
        storageReference        = FirebaseStorage.getInstance().getReference(User.UPLOADS);

        initEditProfile(view);
        // set the content
        updateInfo();

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditMode) {
                    prepForProfileEdit();
                    isEditMode = true;
                } else if (isValidInput()){
                    endEditMode();
                    isEditMode = false;
                } else {
                    Toast.makeText(getContext(), "in valid input!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        return view;
    }

    private void endEditMode() {
        writeStudent();
        updateInfo();
        swapVisibility(View.VISIBLE, View.GONE);
    }

    private void writeStudent() {
        Log.d(TAG, "writeStudent");

        mStudent.setFirstName(firstName);
        mStudent.setLastName(lastName);
        mStudent.setPhoneNumber(phone);
        mStudent.setCity(city);

        db.collection("Students")
                .document(mStudent.getID())
                .set(mStudent)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to update the Teacher! " + e);
                    }
                });
    }


    private boolean isValidInput(){
        Log.d(TAG, "isValidInput");
        boolean isValid = true;

        name = editTextName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        city = editTextCity.getText().toString().trim();

        if(name.isEmpty() || !name.contains(" ")) {
            editTextName.setError(getString(R.string.name_error));
            isValid = false;
        }else{
            firstName = name.split(" ", 2)[0];
            lastName = name.split(" ", 2)[1];
            if(firstName.isEmpty() || lastName.isEmpty()){
                editTextName.setError(getString(R.string.name_error));
                isValid = false;
            }
        }
        if(phone.isEmpty() || phone.length() != 10) {
            editTextPhone.setError(getString(R.string.phone_error));
            isValid = false;
        }
        if(city.isEmpty()) {
            editTextCity.setError(getString(R.string.city_error));
            isValid = false;
        }

        return isValid;
    }

    private void prepForProfileEdit() {
        swapVisibility(View.GONE, View.VISIBLE);
    }

    private void initEditProfile (View v) {
        Log.d(TAG, "initEditProfile");
        editTextName = v.findViewById(R.id.ProfileEditTextName);
        editTextPhone = v.findViewById(R.id.editTextStudentProfilePhone);
        editTextCity = v.findViewById(R.id.editTextStudentProfileCity);
        swapVisibility(View.VISIBLE, View.GONE);
    }

    private void swapVisibility(int tvCode, int etCode) {
        Log.d(TAG, "swap visibility textViewCode[" + tvCode + "] editTextCode[" + etCode +"]");

        username.setVisibility(tvCode);
        editTextName.setVisibility(etCode);

        textViewPhoneNumber.setVisibility(tvCode);
        editTextPhone.setVisibility(etCode);

        textViewCity.setVisibility(tvCode);
        editTextCity.setVisibility(etCode);
    }

    private void updateInfo() {
        Log.d(TAG, "updateInfo");
        textViewPhoneNumber.setText(mStudent.getPhoneNumber());
        editTextPhone.setText(mStudent.getPhoneNumber());

        textViewEmail.setText(mStudent.getEmail());

        textViewCity.setText(mStudent.getCity());
        editTextCity.setText(mStudent.getCity());

        username.setText(mStudent.getFullName());
        editTextName.setText(mStudent.getFullName());

        if (mStudent.getImageUrl() == null || mStudent.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            image_profile.setImageResource(R.mipmap.ic_user_round);
        } else if (getContext() != null) {
            Glide.with(getContext()).load(mStudent.getImageUrl()).into(image_profile);
        }
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
