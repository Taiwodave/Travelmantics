package com.example.travelmantics;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseStorage Storage;
    public static StorageReference StorageRef;
    public static FirebaseAuth.AuthStateListener AuthListener;
    public static DealActivity caller;
    private static final int RC_SIGN_IN = 123;
    public static boolean isAdmin;

    public static ArrayList<TravelDeal> mDeals;

    private FirebaseUtil(){}

    public static void openFbReference(String ref, final DealActivity callerActivity){
        if (firebaseUtil == null){
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            AuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.signIn();
                    }
                    else{
                        String userid = firebaseAuth.getUid();
                        checkAdmin(userid);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }
        mDeals = new ArrayList<TravelDeal>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());



// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void checkAdmin(String uid){
        FirebaseUtil.isAdmin=false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administator")
                .child(uid);
        ChildEventListener Listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin= true;
                caller.showMenu();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
       ref.addChildEventListener(Listener);
    }

    public static void connectStorage(){
        Storage = FirebaseStorage.getInstance();
        StorageRef = Storage.getReference().child("deals_pictures");

    }


    public static void attachListner(){
        firebaseAuth.addAuthStateListener(AuthListener);
    }

    public static void detachListener(){
        firebaseAuth.removeAuthStateListener(AuthListener);
    }
}
