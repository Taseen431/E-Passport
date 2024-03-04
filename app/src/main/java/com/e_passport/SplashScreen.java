package com.e_passport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.e_passport.activities.ApplyActivity;
import com.e_passport.activities.LoginActivity;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.PrefsUtilities;
import com.google.firebase.FirebaseApp;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        PrefsUtilities.init(this);

        if(PrefsUtilities.getPrefs(PrefsUtilities.FIRST_TIME, true)) {
            FirebaseApp.initializeApp(this);
            PrefsUtilities.setPrefs(PrefsUtilities.FIRST_TIME, false);
        }

        FirebaseUtilities.init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, FirebaseUtilities.getUser() != null ? MainActivity.class : LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}