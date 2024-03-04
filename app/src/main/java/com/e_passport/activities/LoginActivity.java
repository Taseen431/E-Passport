package com.e_passport.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.e_passport.MainActivity;
import com.e_passport.R;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.ValidationUtilities;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity implements TextWatcher, OnCompleteListener, OnCanceledListener, OnFailureListener {

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!AppUtilities.isInternetAvailable(this)){
            AppUtilities.showNoInternetDialog(this);
        }

        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    AppUtilities.showLoadingDialog(LoginActivity.this, getResources().getString(R.string.logging_in_msg));
                    FirebaseUtilities.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this).addOnCanceledListener(LoginActivity.this).addOnFailureListener(LoginActivity.this);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            AppUtilities.hideLoadingDialog();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            try {
                throw task.getException();
            } catch (FirebaseAuthException e) {
                AppUtilities.hideLoadingDialog();
                String errorCode = e.getErrorCode();
                if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                    emailInputLayout.setError(getResources().getString(R.string.email_error_hint));
                } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                    passwordEditText.setError(getResources().getString(R.string.password_wrong_error_hint));
                } else {
                    emailInputLayout.setError(errorCode);
                    passwordEditText.setError(errorCode);
                }
            } catch (Exception e) {
                AppUtilities.hideLoadingDialog();
                emailInputLayout.setError(e.getMessage());
                passwordInputLayout.setError(e.getMessage());
            }
        }
    }

    @Override
    public void onCanceled() {
        AppUtilities.updateLoadingDialog(getResources().getString(R.string.logging_in_error_msg));
        AppUtilities.hideLoadingDialog(3000);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        AppUtilities.updateLoadingDialog(e.getMessage());
        AppUtilities.hideLoadingDialog(3000);
        emailInputLayout.setError(e.getMessage());
        passwordInputLayout.setError(e.getMessage());
    }
}