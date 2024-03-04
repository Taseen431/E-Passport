package com.e_passport.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.e_passport.MainActivity;
import com.e_passport.R;
import com.e_passport.models.User;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.utilities.CalculationUtilities;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.ValidationUtilities;
import com.e_passport.utilities.interfaces.ImagePathReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity implements TextWatcher, OnFailureListener {
    private static final int MINIMUM_AGE = 5;

    private Bitmap userProfileBitmap = null;
    private CircleImageView userProfileImage;
    private TextInputLayout nameInputLayout;
    private TextInputEditText nameEditText;
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordEditText;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout dateOfBirthLayout;
    private EditText dateOfBirthEditText;
    private TextInputLayout genderLayout;
    private RadioGroup genderRadioGroup;
    private String selectedGender;
    private MaterialButton signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userProfileImage = findViewById(R.id.user_profile_image);
        nameInputLayout = findViewById(R.id.nameLayout);
        nameEditText = findViewById(R.id.nameEditText);
        emailInputLayout = findViewById(R.id.emailLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordInputLayout = findViewById(R.id.passwordLayout);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        dateOfBirthLayout = findViewById(R.id.dateOfBirthLayout);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        genderLayout = findViewById(R.id.genderLayout);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        signUpButton = findViewById(R.id.signUpButton);

        nameEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        confirmPasswordEditText.addTextChangedListener(this);
        dateOfBirthEditText.addTextChangedListener(this);

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtilities.openImageChooser(SignUpActivity.this, new ImagePathReceiver(){
                    @Override
                    public void onImagePathError() {
                        userProfileBitmap = null;
                        userProfileImage.setImageResource(R.drawable.ic_user);
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.profile_image_chooser_image_invalid_toast), Toast.LENGTH_LONG).show();
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

        dateOfBirthEditText.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Calendar.getInstance().getTime()));

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
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

        selectedGender = getResources().getString(R.string.gender_male_hint);
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

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    AppUtilities.showLoadingDialog(SignUpActivity.this, getResources().getString(R.string.signing_up_msg));
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    FirebaseUtilities.getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AppUtilities.updateLoadingDialog(getResources().getString(R.string.creating_user_msg));

                                String profileImage64bit = AppUtilities.bitmapToBase64(userProfileBitmap);
                                String name = nameEditText.getText().toString();
                                String email = emailEditText.getText().toString();
                                String dateOfBirth = dateOfBirthEditText.getText().toString();

                                User user = new User(profileImage64bit, name, email, dateOfBirth, CalculationUtilities.calculateAge(dateOfBirth), selectedGender, "", "", "", "", "", getResources().getString(R.string.did_not_apply_status));

                                FirebaseUtilities.getReference().child("users").child(FirebaseUtilities.getAuth().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()){
                                            AppUtilities.hideLoadingDialog();
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            AppUtilities.updateLoadingDialog(getResources().getString(R.string.creating_user_error_msg));
                                            AppUtilities.hideLoadingDialog(3000);
                                        }
                                    }
                                }).addOnFailureListener(SignUpActivity.this);
                            } else {
                                AppUtilities.hideLoadingDialog();
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthException e) {
                                    String errorCode = e.getErrorCode();
                                    String errorMessage = e.getMessage();
                                    if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                                        emailInputLayout.setError(getResources().getString(R.string.email_error_hint));
                                    } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                        passwordInputLayout.setError(getResources().getString(R.string.password_wrong_error_hint));
                                    } else {
                                        emailInputLayout.setError(errorMessage);
                                        passwordInputLayout.setError(errorMessage);
                                    }
                                } catch (Exception e) {
                                    emailInputLayout.setError(e.getMessage());
                                    passwordInputLayout.setError(e.getMessage());
                                }
                            }
                        }
                    }).addOnFailureListener(SignUpActivity.this);
                }
            }
        });
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
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
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

        if (password.isEmpty()) {
            passwordInputLayout.setError(getResources().getString(R.string.password_error_hint));
            isValid = false;
        } else if (!ValidationUtilities.isStrongPassword(password)) {
            passwordInputLayout.setError(getResources().getString(R.string.password_strong_error_hint));
            isValid = false;
        } else {
            passwordInputLayout.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError(getResources().getString(R.string.confirm_password_error_hint));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError(getResources().getString(R.string.confirm_password_match_error_hint));
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
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
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);
        dateOfBirthLayout.setError(null);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        AppUtilities.updateLoadingDialog(e.getMessage());
        AppUtilities.hideLoadingDialog(3000);
        emailInputLayout.setError(e.getMessage());
        passwordInputLayout.setError(e.getMessage());
    }
}