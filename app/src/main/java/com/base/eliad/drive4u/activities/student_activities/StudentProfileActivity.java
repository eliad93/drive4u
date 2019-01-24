package com.base.eliad.drive4u.activities.student_activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.base_activities.StudentBaseActivity;
import com.base.eliad.drive4u.models.Student;
import com.base.eliad.drive4u.models.User;
import com.bumptech.glide.Glide;
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

public class StudentProfileActivity extends StudentBaseActivity {

    private static final String TAG = StudentProfileActivity.class.getName();

    CircleImageView image_profile;
    TextView username;
    private ImageButton editBtn;
    private ImageView editPicBtn;
    private TextView textViewPhoneNumber, textViewEmail, textViewCity;

    private EditText editTextName, editTextPhone, editTextCity;

    private String name, firstName="", lastName="", phone, city;

    private boolean isEditMode;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // init text views
        image_profile           = findViewById(R.id.profile_image);
        editPicBtn              = findViewById(R.id.imageViewEditPhoto);
        username                = findViewById(R.id.profile_username);
        isEditMode = false;
        editBtn = findViewById(R.id.student_imageButtonEdit);
        textViewPhoneNumber     = findViewById(R.id.StudentProfilePhone);
        textViewEmail           = findViewById(R.id.StudentProfileEmail);
        textViewCity            = findViewById(R.id.StudentProfileCity);
        // get ready for loading pictures
        storageReference        = FirebaseStorage.getInstance().getReference(User.UPLOADS);

        initEditProfile();
        // set the content
        updateInfo();

        editPicBtn.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(StudentProfileActivity.this, "in valid input!", Toast.LENGTH_SHORT).show();
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

    private void initEditProfile () {
        Log.d(TAG, "initEditProfile");
        editTextName = findViewById(R.id.ProfileEditTextName);
        editTextPhone = findViewById(R.id.editTextStudentProfilePhone);
        editTextCity = findViewById(R.id.editTextStudentProfileCity);
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
        } else {
            Glide.with(this).load(mStudent.getImageUrl()).into(image_profile);
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
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
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
                        Log.d(TAG, "Fail to write the picture!");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}
