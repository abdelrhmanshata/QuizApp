package com.example.quizapp.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.quizapp.Activity.UI.Doctor.DoctorMainActivity;
import com.example.quizapp.Activity.UI.Student.StudentMainActivity;
import com.example.quizapp.Model.ModelUser;
import com.example.quizapp.R;
import com.example.quizapp.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser CurrentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListener();
    }

    void onClickListener() {
        binding.btnRegisterNow.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
        binding.loginBtn.setOnClickListener(v -> {
            checkUserInput();
        });
        binding.btnForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });
    }

    void checkUserInput() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.emailIsRequired));
            binding.inputEmail.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError(getString(R.string.please_enter_valid_email));
            binding.inputEmail.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.passwordIsRequired));
            binding.inputPassword.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError(getString(R.string.minimumLength));
            binding.inputPassword.requestFocus();
            binding.progressCircular.setVisibility(View.INVISIBLE);
            return;
        }

        login(inputEmail, inputPassword);
    }

    void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CurrentUser = firebaseAuth.getCurrentUser();
                binding.progressCircular.setVisibility(View.INVISIBLE);
                getUserData();

            } else if (task.getException() instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                Toast.makeText(this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
            } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                Toast.makeText(this, getString(R.string.passwordIncorrect), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error+->" + task.getException(), Toast.LENGTH_SHORT).show();
            }
            binding.progressCircular.setVisibility(View.INVISIBLE);
        });
    }

    void getUserData() {
        refUsers.child(CurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                if (user != null) {
                    if (user.getUserType().equals("Doctor")) {
                        startActivity(new Intent(LoginActivity.this, DoctorMainActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, StudentMainActivity.class));
                    }
                    Toast.makeText(LoginActivity.this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(LoginActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}