package com.e_passport.utilities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtilities {
    private static FirebaseAuth auth;
    private static DatabaseReference mDatabase;
    public static void init(){
         auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseAuth getAuth(){
        return auth;
    }

    public static FirebaseUser getUser(){
        return auth.getCurrentUser();
    }

    public static DatabaseReference getReference() {
        return mDatabase;
    }
}
