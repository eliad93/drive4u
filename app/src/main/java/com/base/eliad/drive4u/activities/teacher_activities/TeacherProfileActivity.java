package com.base.eliad.drive4u.activities.teacher_activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.base.eliad.drive4u.R;
import com.base.eliad.drive4u.base_activities.TeacherBaseNavigationActivity;
import com.base.eliad.drive4u.models.Teacher;
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

public class TeacherProfileActivity extends TeacherBaseNavigationActivity
        implements AdapterView.OnItemSelectedListener{

    private static final String TAG = TeacherProfileActivity.class.getName();

    CircleImageView image_profile;
    TextView username;

    private ImageButton editInfoBtn;
    private ImageView editPicBtn;
    private TextView textViewPhoneNumber, textViewEmail, textViewCity, textViewCarModel,
            textViewGearType, textViewPrice;

    private EditText editTextName, editTextPhone, editTextCity, editTextCarModel, editTextPrice;
    private Spinner spinnerGearType;

    private String gearType, name, firstName="", lastName="",
            phone, city, carModel, priceString;

    private boolean isEditMode;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = findViewById(R.id.teacher_base_content_frame);
        getLayoutInflater().inflate(R.layout.activity_teacher_profile, frameLayout);

        storageReference = FirebaseStorage.getInstance().getReference(User.UPLOADS);
        isEditMode = false;
        image_profile = findViewById(R.id.profile_image);
        editInfoBtn = findViewById(R.id.imageButtonEdit);
        username = findViewById(R.id.profile_username);
        textViewPhoneNumber = findViewById(R.id.TeacherProfilePhone);
        textViewEmail = findViewById(R.id.TeacherProfileEmail);
        textViewCity = findViewById(R.id.TeacherProfileCity);
        textViewCarModel = findViewById(R.id.TeacherProfileCarModel);
        textViewGearType = findViewById(R.id.TeacherProfileGearType);
        textViewPrice = findViewById(R.id.TeacherProfilePrice);
        editPicBtn = findViewById(R.id.imageViewEditPhoto);

        initEditProfile();

        editPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        editInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditMode) {
                    prepForProfileEdit();
                    isEditMode = true;
                } else if (isValidInput()){
                    endEditMode();
                    isEditMode = false;
                } else {
                    Toast.makeText(TeacherProfileActivity.this, "Please enter a valid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateProfile();
        db.collection("Teachers")
                .document(mTeacher.getID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listen failed: " + e);
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            mTeacher = documentSnapshot.toObject(Teacher.class);
                            updateProfile();
                        }
                    }
                });

    }

    private void endEditMode() {
        updateTeacher();
        updateProfile();
        swapVisibility(View.VISIBLE, View.GONE);
    }

    private boolean isValidInput(){
        Log.d(TAG, "isValidInput");
        boolean isValid = true;

        name = editTextName.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();
        city = editTextCity.getText().toString().trim();
        carModel = editTextCarModel.getText().toString().trim();
        priceString = editTextPrice.getText().toString().trim();

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
        if(carModel.isEmpty()) {
            editTextCarModel.setError(getString(R.string.car_model_error));
            isValid = false;
        }
        if(priceString.isEmpty()) {
            editTextPrice.setError(getString(R.string.please_enter_price));
            isValid = false;
        }
        if(gearType == null) {
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
        editTextPhone = findViewById(R.id.editTextTeacherProfilePhone);
        editTextCity = findViewById(R.id.editTextTeacherProfileCity);
        editTextCarModel = findViewById(R.id.editTextTeacherProfileCarModel);
        editTextPrice = findViewById(R.id.editTextTeacherProfilePrice);
        spinnerGearType = findViewById(R.id.spinnerTeacherProfileGearType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gear_types_teacher, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGearType.setAdapter(adapter);
        spinnerGearType.setOnItemSelectedListener(this);

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

        textViewCarModel.setVisibility(tvCode);
        editTextCarModel.setVisibility(etCode);

        textViewPrice.setVisibility(tvCode);
        editTextPrice.setVisibility(etCode);

        textViewGearType.setVisibility(tvCode);
        spinnerGearType.setVisibility(etCode);
    }

    private void updateProfile() {
        username.setText(mTeacher.getFullName());
        editTextName.setText(mTeacher.getFullName());
        textViewPhoneNumber.setText(mTeacher.getPhoneNumber());
        editTextPhone.setText(mTeacher.getPhoneNumber());
        textViewEmail.setText(mTeacher.getEmail());
        textViewCity.setText(mTeacher.getCity());
        editTextCity.setText(mTeacher.getCity());
        textViewCarModel.setText(mTeacher.getCarModel());
        editTextCarModel.setText(mTeacher.getCarModel());
        textViewGearType.setText(mTeacher.getGearType());
        String text = Integer.toString(mTeacher.getPrice());
        textViewPrice.setText(text);
        editTextPrice.setText(text);

        if (mTeacher.getImageUrl() == null || mTeacher.getImageUrl().equals(User.DEFAULT_IMAGE_KEY)) {
            image_profile.setImageResource(R.mipmap.ic_user_foreground );
        } else {
            Glide.with(this).load(mTeacher.getImageUrl()).into(image_profile);
        }
    }

    private void updateTeacher() {
        Log.d(TAG, "updateTeacher");

        mTeacher.setFirstName(firstName);
        mTeacher.setLastName(lastName);
        mTeacher.setPhoneNumber(phone);
        mTeacher.setCity(city);
        mTeacher.setCarModel(carModel);
        mTeacher.setGearType(gearType);
        mTeacher.setPrice(Integer.parseInt(priceString));

        db.collection("Teachers")
                .document(mTeacher.getID())
                .set(mTeacher)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to update the Teacher! " + e);
                    }
                });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
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

                        mTeacher.setImageUrl(mUri);
                        db.collection(getString(R.string.DB_Teachers))
                                .document(mTeacher.getID()).update("imageUrl", mUri);

                        pd.dismiss();
                    } else {
                        Toast.makeText(TeacherProfileActivity.this, "Fail to write the picture!", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TeacherProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(TeacherProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected");
        gearType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gearType = null;
    }

}
