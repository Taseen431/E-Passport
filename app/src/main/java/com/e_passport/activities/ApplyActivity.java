package com.e_passport.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.e_passport.R;
import com.e_passport.models.User;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.utilities.CalculationUtilities;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.PrefsUtilities;
import com.e_passport.utilities.ValidationUtilities;
import com.e_passport.utilities.interfaces.ImagePathReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplyActivity extends AppCompatActivity implements TextWatcher, OnFailureListener {
    private static final int MINIMUM_AGE = 5;

    private Bitmap userProfileBitmap = null;
    private CircleImageView userProfileImage;
    private TextInputLayout nameInputLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout phoneNumberInputLayout;
    private TextInputEditText phoneNumberEditText;
    private TextInputLayout addressInputLayout;
    private TextInputEditText addressEditText;
    private TextInputLayout nidNumberLayout;
    private TextInputEditText nidNumberEditText;
    private View nidContainer;
    private View nidFront;
    private ImageView nidFrontImageView;
    private Bitmap nidFrontBmp = null;
    private View nidBack;
    private ImageView nidBackImageView;
    private Bitmap nidBackBmp = null;
    private TextInputLayout dateOfBirthLayout;
    private EditText dateOfBirthEditText;
    private TextInputLayout genderLayout;
    private RadioGroup genderRadioGroup;
    private String selectedGender;
    private MaterialButton applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        userProfileImage = findViewById(R.id.user_profile_image);
        nameInputLayout = findViewById(R.id.nameLayout);
        nameEditText = findViewById(R.id.nameEditText);
        emailInputLayout = findViewById(R.id.emailLayout);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberLayout);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        addressInputLayout = findViewById(R.id.addressLayout);
        addressEditText = findViewById(R.id.addressEditText);
        nidNumberLayout = findViewById(R.id.nidNumberLayout);
        nidNumberEditText = findViewById(R.id.nidNumberEditText);
        nidContainer = findViewById(R.id.nid_container);
        nidFront = findViewById(R.id.nid_front);
        nidBack = findViewById(R.id.nid_back);
        nidFrontImageView = findViewById(R.id.nid_front_image);
        nidBackImageView = findViewById(R.id.nid_back_image);
        dateOfBirthLayout = findViewById(R.id.dateOfBirthLayout);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        genderLayout = findViewById(R.id.genderLayout);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        applyButton = findViewById(R.id.applyButton);

        nameEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);
        phoneNumberEditText.addTextChangedListener(this);
        addressEditText.addTextChangedListener(this);
        nidNumberEditText.addTextChangedListener(this);
        dateOfBirthEditText.addTextChangedListener(this);

        String profileImg64bit = PrefsUtilities.getPrefs("profileImg64bit", "");
        if(!profileImg64bit.trim().isEmpty()) {
            userProfileImage.setImageBitmap(AppUtilities.base64ToBitmap(profileImg64bit));
        }

        nameEditText.setText(PrefsUtilities.getPrefs("name", ""));
        emailEditText.setText(PrefsUtilities.getPrefs("email", ""));
        dateOfBirthEditText.setText(PrefsUtilities.getPrefs("dateOfBirth", new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime())));

        selectedGender = PrefsUtilities.getPrefs("gender", "Male");

        if(selectedGender.equalsIgnoreCase(getResources().getString(R.string.gender_male_hint))) {
            ((MaterialRadioButton) findViewById(R.id.maleRadioButton)).setChecked(true);
            ((MaterialRadioButton) findViewById(R.id.femaleRadioButton)).setChecked(false);
            ((MaterialRadioButton) findViewById(R.id.othersRadioButton)).setChecked(false);
        } else if(selectedGender.equalsIgnoreCase(getResources().getString(R.string.gender_female_hint))){
            ((MaterialRadioButton) findViewById(R.id.maleRadioButton)).setChecked(false);
            ((MaterialRadioButton) findViewById(R.id.femaleRadioButton)).setChecked(true);
            ((MaterialRadioButton) findViewById(R.id.othersRadioButton)).setChecked(false);
        } else if(selectedGender.equalsIgnoreCase(getResources().getString(R.string.gender_others_hint))){
            ((MaterialRadioButton) findViewById(R.id.maleRadioButton)).setChecked(false);
            ((MaterialRadioButton) findViewById(R.id.femaleRadioButton)).setChecked(false);
            ((MaterialRadioButton) findViewById(R.id.othersRadioButton)).setChecked(true);
        }

        phoneNumberEditText.setText(PrefsUtilities.getPrefs("phoneNumber", ""));
        addressEditText.setText(PrefsUtilities.getPrefs("address", ""));
        nidNumberEditText.setText(PrefsUtilities.getPrefs("nidNumber", ""));

        String nidFont64bit = PrefsUtilities.getPrefs("nidFont64bit", "");
        if(!nidFont64bit.isEmpty()) {
            nidFrontImageView.setImageBitmap(AppUtilities.base64ToBitmap(nidFont64bit));
        }

        String nidBack64bit = PrefsUtilities.getPrefs("nidBack64bit", "");
        if(!nidBack64bit.isEmpty()) {
            nidBackImageView.setImageBitmap(AppUtilities.base64ToBitmap(nidBack64bit));
        }

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openImageChooser(ApplyActivity.this, new ImagePathReceiver(){
                    @Override
                    public void onImagePathError() {
                        userProfileBitmap = null;
                        userProfileImage.setImageResource(R.drawable.ic_user);
                        Toast.makeText(ApplyActivity.this, getResources().getString(R.string.profile_image_chooser_image_invalid_toast), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onImagePathReceived(String path) {
                        AppUtilities.resizeAndCompressImage(path, 150, 150);
                        userProfileBitmap = BitmapFactory.decodeFile(path);
                        userProfileImage.setImageBitmap(userProfileBitmap);
                    }
                });
            }
        });

        nidFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtilities.openImageChooser(ApplyActivity.this, new ImagePathReceiver(){
                    @Override
                    public void onImagePathError() {
                        nidFrontBmp = null;
                        nidFrontImageView.setImageResource(R.drawable.ic_nid_front);
                        Toast.makeText(ApplyActivity.this, getResources().getString(R.string.nid_front_image_chooser_image_invalid_toast), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onImagePathReceived(String path) {
                        AppUtilities.resizeAndCompressImage(path, 310, 320);
                        nidFrontBmp = BitmapFactory.decodeFile(path);
                        nidFrontImageView.setImageBitmap(nidFrontBmp);
                    }
                });
            }
        });

        nidBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtilities.openImageChooser(ApplyActivity.this, new ImagePathReceiver(){
                    @Override
                    public void onImagePathError() {
                        nidBackBmp = null;
                        nidBackImageView.setImageResource(R.drawable.ic_nid_back);
                        Toast.makeText(ApplyActivity.this, getResources().getString(R.string.nid_back_image_chooser_image_invalid_toast), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onImagePathReceived(String path) {
                        AppUtilities.resizeAndCompressImage(path, 310, 320);
                        nidBackBmp = BitmapFactory.decodeFile(path);
                        nidBackImageView.setImageBitmap(nidBackBmp);
                    }
                });
            }
        });


        dateOfBirthEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                Date date;
                try {
                    date = dateFormat.parse(dateOfBirthEditText.getText().toString());
                } catch (ParseException e) {
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                String selectedDate = AppUtilities.preZeroFormat(day) + "-" + AppUtilities.preZeroFormat(month + 1) + "-" + year;
                                dateOfBirthEditText.setText(selectedDate);
                                if (!ValidationUtilities.isDateOfBirthValid(selectedDate, MINIMUM_AGE)) {
                                    dateOfBirthLayout.setError(getResources().getString(R.string.date_of_birth_error_hint));
                                }
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadioButton) {
                    selectedGender = getResources().getString(R.string.gender_male_hint);
                } else if (checkedId == R.id.femaleRadioButton) {
                    selectedGender = getResources().getString(R.string.gender_female_hint);;
                } else if (checkedId == R.id.othersRadioButton) {
                    selectedGender = getResources().getString(R.string.gender_others_hint);;
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    if(getIntent().getBooleanExtra("isForUpdate", false)){
                        AppUtilities.showLoadingDialog(ApplyActivity.this, getResources().getString(R.string.updating_user_msg));
                    } else {
                        AppUtilities.showLoadingDialog(ApplyActivity.this, getResources().getString(R.string.creating_user_msg));
                    }

                    String profileImage64bit = PrefsUtilities.getPrefs("profileImg64bit", "");
                    if(userProfileBitmap != null){
                        profileImage64bit = AppUtilities.bitmapToBase64(userProfileBitmap);
                    }

                    String name = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String phoneNumber = phoneNumberEditText.getText().toString();
                    String address = addressEditText.getText().toString();
                    String nidNumber = nidNumberEditText.getText().toString();
                    String dateOfBirth = dateOfBirthEditText.getText().toString();

                    String nidFrontImage64bit = PrefsUtilities.getPrefs("nidFront64bit", "");
                    if(nidFrontBmp != null ) {
                        nidFrontImage64bit = AppUtilities.bitmapToBase64(nidFrontBmp);
                    }

                    String nidBackImage64bit = PrefsUtilities.getPrefs("nidBack64bit", "");
                    if(nidFrontBmp != null ) {
                        nidBackImage64bit = AppUtilities.bitmapToBase64(nidBackBmp);
                    }

                    String status = PrefsUtilities.getPrefs("passportStatus", "");

                    if(getIntent().getBooleanExtra("isForUpdate", true)){
                        status = getResources().getString(R.string.pending_status);
                    }

                    User user = new User(profileImage64bit, name, email, dateOfBirth, CalculationUtilities.calculateAge(dateOfBirth), selectedGender, phoneNumber, address, nidNumber, nidFrontImage64bit, nidBackImage64bit, status);

                    FirebaseUtilities.getReference().child("users").child(FirebaseUtilities.getAuth().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()){
                                AppUtilities.hideLoadingDialog();
                                finish();
                            } else {
                                if(getIntent().getBooleanExtra("isForUpdate", false)){
                                    AppUtilities.updateLoadingDialog(getResources().getString(R.string.updating_user_error_msg));
                                } else {
                                    AppUtilities.updateLoadingDialog(getResources().getString(R.string.creating_user_error_msg));
                                }

                                AppUtilities.hideLoadingDialog(3000);
                            }
                        }
                    }).addOnFailureListener(ApplyActivity.this);
                }
            }
        });

        if(getIntent().getBooleanExtra("isForUpdate", false)){
            nidContainer.setVisibility(View.GONE);
            applyButton.setText(getResources().getText(R.string.update_user_update_btn));
        } else {
            nidContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppUtilities.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AppUtilities.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private boolean validateInput() {
        boolean isValid = true;

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String nidNumber = nidNumberEditText.getText().toString().trim();
        String dateOfBirth = dateOfBirthEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameInputLayout.setError(getResources().getString(R.string.name_error_hint));
            isValid = false;
        } else {
            nameInputLayout.setError(null);
        }

        if (email.isEmpty() || !ValidationUtilities.isValidEmail(email)) {
            emailInputLayout.setError(getResources().getString(R.string.email_error_hint));
            isValid = false;
        } else {
            emailInputLayout.setError(null);
        }

        if (phoneNumber.isEmpty() || !ValidationUtilities.isValidPhoneNumber(phoneNumber)) {
            phoneNumberInputLayout.setError(getResources().getString(R.string.phone_number_error_hint));
            isValid = false;
        } else {
            phoneNumberInputLayout.setError(null);
        }

        if (address.isEmpty() || address.length() < 3) {
            addressInputLayout.setError(getResources().getString(R.string.address_error_hint));
            isValid = false;
        } else {
            addressInputLayout.setError(null);
        }

        if (nidNumber.isEmpty() || !ValidationUtilities.isValidNidNumber(nidNumber)) {
            nidNumberLayout.setError(getResources().getString(R.string.nid_number_error_hint));
            isValid = false;
        } else {
            nidNumberLayout.setError(null);
        }

        if (dateOfBirth.isEmpty() || !ValidationUtilities.isDateOfBirthValid(dateOfBirth, MINIMUM_AGE)) {
            dateOfBirthLayout.setError(getResources().getString(R.string.date_of_birth_error_hint));
            isValid = false;
        } else {
            dateOfBirthLayout.setError(null);
        }

        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        phoneNumberInputLayout.setError(null);
        addressInputLayout.setError(null);
        nidNumberLayout.setError(null);
        dateOfBirthLayout.setError(null);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        AppUtilities.updateLoadingDialog(e.getMessage());
        AppUtilities.hideLoadingDialog(3000);
    }
}