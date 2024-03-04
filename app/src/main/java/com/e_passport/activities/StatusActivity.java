package com.e_passport.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.e_passport.R;
import com.e_passport.utilities.PrefsUtilities;

public class StatusActivity extends AppCompatActivity {

    private TextView currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        currentStatus = (TextView) findViewById(R.id.current_status);
        String status = PrefsUtilities.getPrefs("passportStatus", "");
        currentStatus.setText(status.isEmpty() ? getResources().getString(R.string.did_not_apply_status) : status);
    }
}