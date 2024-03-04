package com.e_passport.pages;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.e_passport.R;
import com.e_passport.activities.ApplyActivity;
import com.e_passport.activities.LoginActivity;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.PrefsUtilities;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPage extends Fragment {
    public static UserPage newInstance() {
        return new UserPage();
    }
    private SwipeRefreshLayout swipeRefreshLayout;

    private CircleImageView userProfileCircleImageView;
    private TextView userNameTextView;
    private TextView emailTextView;
    private TextView dateOfBirthTextView;
    private TextView ageTextView;
    private TextView genderTextView;
    private TextView phoneNumberTextView;
    private TextView nidNumberTextView;
    private TextView addressTextView;
    private ImageView nidFrontImageView;
    private ImageView nidBackImageView;
    private MaterialButton updateButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.user_page, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        userProfileCircleImageView = rootView.findViewById(R.id.user_profile);
        userNameTextView = rootView.findViewById(R.id.user_name);
        emailTextView = rootView.findViewById(R.id.user_email);
        dateOfBirthTextView = rootView.findViewById(R.id.user_date_of_birth);
        ageTextView = rootView.findViewById(R.id.user_age);
        genderTextView = rootView.findViewById(R.id.user_gender);
        phoneNumberTextView = rootView.findViewById(R.id.user_phone_number);
        addressTextView = rootView.findViewById(R.id.user_address);
        nidNumberTextView = rootView.findViewById(R.id.user_nid_number);
        nidFrontImageView = rootView.findViewById(R.id.nid_front);
        nidBackImageView = rootView.findViewById(R.id.nid_back);
        updateButton = rootView.findViewById(R.id.updateButton);

        userProfileCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileImg64bit = PrefsUtilities.getPrefs("profileImg64bit", "");
                if(!profileImg64bit.trim().isEmpty()) {
                    showImageViewerDialog(AppUtilities.base64ToBitmap(profileImg64bit));
                } else {
                    showImageViewerDialog(R.drawable.ic_user);
                }
            }
        });

        nidFrontImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nidFront64bit = PrefsUtilities.getPrefs("nidFront64bit", "");
                if(!nidFront64bit.isEmpty()) {
                    showImageViewerDialog(AppUtilities.base64ToBitmap(nidFront64bit));
                } else {
                    showImageViewerDialog(R.drawable.ic_nid_front);
                }
            }
        });

        nidBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nidBack64bit = PrefsUtilities.getPrefs("nidBack64bit", "");
                if(!nidBack64bit.trim().isEmpty()) {
                    showImageViewerDialog(AppUtilities.base64ToBitmap(nidBack64bit));
                } else {
                    showImageViewerDialog(R.drawable.ic_nid_back);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ApplyActivity.class);
                intent.putExtra("isForUpdate", true);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        swipeRefreshLayout.setRefreshing(true);
        if(FirebaseUtilities.getUser() != null){
            FirebaseUtilities.getReference().child("users").child(FirebaseUtilities.getAuth().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        String key = childSnapshot.getKey();
                        Object value = childSnapshot.getValue();
                        PrefsUtilities.setPrefs(key, value.toString());
                    }

                    String profileImg64bit = PrefsUtilities.getPrefs("profileImg64bit", "");
                    if(!profileImg64bit.isEmpty()) {
                        userProfileCircleImageView.setImageBitmap(AppUtilities.base64ToBitmap(profileImg64bit));
                    }

                    userNameTextView.setText(PrefsUtilities.getPrefs("name", ""));
                    emailTextView.setText(PrefsUtilities.getPrefs("email", ""));
                    dateOfBirthTextView.setText(PrefsUtilities.getPrefs("dateOfBirth", ""));
                    ageTextView.setText(PrefsUtilities.getPrefs("age", ""));
                    genderTextView.setText(PrefsUtilities.getPrefs("gender", ""));
                    phoneNumberTextView.setText(PrefsUtilities.getPrefs("phoneNumber", ""));
                    addressTextView.setText(PrefsUtilities.getPrefs("address", ""));

                    nidNumberTextView.setText(PrefsUtilities.getPrefs("nidNumber", ""));
                    String nidFront64bit = PrefsUtilities.getPrefs("nidFront64bit", "");
                    if(!nidFront64bit.isEmpty()) {
                        nidFrontImageView.setImageBitmap(AppUtilities.base64ToBitmap(nidFront64bit));
                    }
                    String nidBack64bit = PrefsUtilities.getPrefs("nidBack64bit", "");
                    if(!nidBack64bit.isEmpty()) {
                        nidBackImageView.setImageBitmap(AppUtilities.base64ToBitmap(nidBack64bit));
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showImageViewerDialog(Bitmap bmpImg){
        final Dialog imageViewerDialog = new Dialog(getActivity());
        imageViewerDialog.setContentView(R.layout.image_viewer);
        ImageView imageView = imageViewerDialog.findViewById(R.id.image_viewer);
        imageView.setImageBitmap(bmpImg);
        imageViewerDialog.setCancelable(true);
        imageViewerDialog.show();
    }

    private void showImageViewerDialog(int imgResId){
        final Dialog imageViewerDialog = new Dialog(getActivity());
        imageViewerDialog.setContentView(R.layout.image_viewer);
        ImageView imageView = imageViewerDialog.findViewById(R.id.image_viewer);
        imageView.setImageResource(imgResId);
        imageViewerDialog.setCancelable(true);
        imageViewerDialog.show();
    }
}