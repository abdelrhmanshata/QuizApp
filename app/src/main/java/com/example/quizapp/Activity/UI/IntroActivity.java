package com.example.quizapp.Activity.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.quizapp.Activity.Auth.LoginActivity;
import com.example.quizapp.Activity.UI.Doctor.DoctorMainActivity;
import com.example.quizapp.Activity.UI.Student.StudentMainActivity;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");

    private final static int SPLASH_DISPLAY_LENGTH = 1000; //change time


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(() -> {
            if (firebaseUser != null) {
                getUserData();
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                ActivityCompat.finishAffinity(this);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    void getUserData() {
        refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    if (user.getUserType().equals("Doctor")) {
                        startActivity(new Intent(IntroActivity.this, DoctorMainActivity.class));
                    } else {
                        startActivity(new Intent(IntroActivity.this, StudentMainActivity.class));
                    }
                    ActivityCompat.finishAffinity(IntroActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(IntroActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}